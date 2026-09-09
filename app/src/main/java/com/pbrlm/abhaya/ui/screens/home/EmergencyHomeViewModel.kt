package com.pbrlm.abhaya.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.BuildConfig
import com.pbrlm.abhaya.data.location.LocationService
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.integration.ZoneDataProvider
import com.pbrlm.abhaya.domain.integration.ZoneInfo
import com.pbrlm.abhaya.domain.model.*
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import com.pbrlm.abhaya.domain.service.EmergencyIdGenerator
import com.pbrlm.abhaya.domain.service.PriorityCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HomeUiState(
    val locationData: LocationData? = null,
    val isLocating: Boolean = true,
    val zoneInfo: ZoneInfo? = null,
    val sosResult: SosResult? = null,
    val showCallDialog: Boolean = false,
    val callDialogNumber: String = "",
    val callDialogTitle: String = ""
)

data class SosResult(
    val emergencyRequestId: String,
    val timestamp: Date,
    val locationData: LocationData,
    val success: Boolean,
    val errorMessage: String? = null
)

@HiltViewModel
class EmergencyHomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationService: LocationService,
    private val emergencyRepository: EmergencyRepository,
    private val zoneDataProvider: ZoneDataProvider,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocating = true) }
            try {
                locationService.observeLocation()
                    .catch { _uiState.update { s -> s.copy(isLocating = false) } }
                    .collect { loc ->
                        _uiState.update { it.copy(locationData = loc, isLocating = false) }
                        // Load zone info for current location
                        loadZoneInfo(loc.latitude, loc.longitude)
                    }
            } catch (e: SecurityException) {
                _uiState.update { it.copy(isLocating = false) }
            }
        }
    }

    private fun loadZoneInfo(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val zone = zoneDataProvider.getZoneForLocation(lat, lng)
                _uiState.update { it.copy(zoneInfo = zone) }
            } catch (_: Exception) { }
        }
    }

    /** SEND SOS — creates a CRITICAL emergency request immediately */
    fun sendSos() {
        viewModelScope.launch {
            val location = _uiState.value.locationData
                ?: locationService.getCurrentLocation()

            val id = EmergencyIdGenerator.generate(isDemoData = false)
            val request = EmergencyRequest(
                emergencyRequestId = id,
                userId = userProvider.getCurrentUserId(),
                emergencyType = EmergencyType.SOS,
                latitude = location.latitude,
                longitude = location.longitude,
                gpsAccuracy = location.accuracy,
                timestamp = Date(),
                priority = PriorityCalculator.calculateForSos(),
                status = EmergencyStatus.ACTIVE
            )

            val result = emergencyRepository.saveEmergencyRequest(request)
            _uiState.update {
                it.copy(
                    sosResult = SosResult(
                        emergencyRequestId = id,
                        timestamp = request.timestamp,
                        locationData = location,
                        success = result.isSuccess,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                )
            }
        }
    }

    fun dismissSosResult() {
        _uiState.update { it.copy(sosResult = null) }
    }

    /** Opens confirmation dialog before dialing */
    fun requestCall112() {
        _uiState.update {
            it.copy(
                showCallDialog = true,
                callDialogNumber = "112",
                callDialogTitle = "Call emergency number 112?"
            )
        }
    }

    fun requestCallRescue() {
        val number = BuildConfig.RESCUE_NUMBER
        _uiState.update {
            it.copy(
                showCallDialog = true,
                callDialogNumber = number,
                callDialogTitle = "Call Rescue ($number)?"
            )
        }
    }

    fun dismissCallDialog() {
        _uiState.update { it.copy(showCallDialog = false) }
    }

    /** Opens Android dialer — user places call themselves */
    fun confirmCall() {
        val number = _uiState.value.callDialogNumber
        dismissCallDialog()
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun refreshLocation() {
        startLocationUpdates()
    }
}
