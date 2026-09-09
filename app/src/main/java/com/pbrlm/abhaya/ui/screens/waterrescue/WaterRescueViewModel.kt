package com.pbrlm.abhaya.ui.screens.waterrescue

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.data.location.LocationService
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.model.*
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import com.pbrlm.abhaya.domain.service.EmergencyIdGenerator
import com.pbrlm.abhaya.domain.service.PriorityCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class WaterRescueFormState(
    val numberOfPeople: Int = 1,
    val waterDepth: WaterDepth = WaterDepth.UNKNOWN,
    val waterRising: Boolean? = null,
    val peopleTrapped: Boolean = false,
    val injured: Boolean = false,
    val medicalRequired: Boolean = false,
    val children: Boolean = false,
    val elderly: Boolean = false,
    val disabled: Boolean = false,
    val buildingFloor: String = "",
    val contactNumber: String = "",
    val additionalInfo: String = "",
    val photoUri: Uri? = null,
    // validation
    val contactError: String = "",
    // submission
    val isLoading: Boolean = false,
    val submittedRequest: EmergencyRequest? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class WaterRescueViewModel @Inject constructor(
    private val locationService: LocationService,
    private val emergencyRepository: EmergencyRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _form = MutableStateFlow(WaterRescueFormState())
    val form: StateFlow<WaterRescueFormState> = _form.asStateFlow()

    fun updateNumberOfPeople(n: Int)         { _form.update { it.copy(numberOfPeople = n) } }
    fun updateWaterDepth(d: WaterDepth)      { _form.update { it.copy(waterDepth = d) } }
    fun updateWaterRising(r: Boolean?)       { _form.update { it.copy(waterRising = r) } }
    fun updatePeopleTrapped(v: Boolean)      { _form.update { it.copy(peopleTrapped = v) } }
    fun updateInjured(v: Boolean)            { _form.update { it.copy(injured = v) } }
    fun updateMedicalRequired(v: Boolean)    { _form.update { it.copy(medicalRequired = v) } }
    fun updateChildren(v: Boolean)           { _form.update { it.copy(children = v) } }
    fun updateElderly(v: Boolean)            { _form.update { it.copy(elderly = v) } }
    fun updateDisabled(v: Boolean)           { _form.update { it.copy(disabled = v) } }
    fun updateBuildingFloor(s: String)       { _form.update { it.copy(buildingFloor = s) } }
    fun updateContactNumber(s: String)       { _form.update { it.copy(contactNumber = s, contactError = "") } }
    fun updateAdditionalInfo(s: String)      { _form.update { it.copy(additionalInfo = s) } }
    fun updatePhoto(uri: Uri?)               { _form.update { it.copy(photoUri = uri) } }
    fun clearSubmission()                    { _form.update { it.copy(submittedRequest = null, errorMessage = null) } }

    fun submit() {
        val f = _form.value

        // Basic validation
        if (f.contactNumber.isNotBlank() && !f.contactNumber.matches(Regex("\\d{10,12}"))) {
            _form.update { it.copy(contactError = "Enter a valid phone number") }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isLoading = true, errorMessage = null) }

            val location = locationService.getCurrentLocation()
            val priority = PriorityCalculator.calculateForWaterRescue(
                waterDepth    = f.waterDepth,
                waterRising   = f.waterRising,
                peopleTrapped = f.peopleTrapped,
                injured       = f.injured,
                numberOfPeople= f.numberOfPeople,
                children      = f.children,
                elderly       = f.elderly,
                disabled      = f.disabled
            )

            val request = EmergencyRequest(
                emergencyRequestId = EmergencyIdGenerator.generate(),
                userId             = userProvider.getCurrentUserId(),
                emergencyType      = EmergencyType.WATER_RESCUE,
                latitude           = location.latitude,
                longitude          = location.longitude,
                gpsAccuracy        = location.accuracy,
                timestamp          = Date(),
                numberOfPeople     = f.numberOfPeople,
                waterDepth         = f.waterDepth,
                waterRising        = f.waterRising,
                peopleTrapped      = f.peopleTrapped,
                injured            = f.injured,
                medicalRequired    = f.medicalRequired,
                children           = f.children,
                elderly            = f.elderly,
                disabled           = f.disabled,
                buildingFloor      = f.buildingFloor.ifBlank { null },
                contactNumber      = f.contactNumber,
                description        = f.additionalInfo,
                photoUrl           = f.photoUri?.toString(),
                priority           = priority,
                status             = EmergencyStatus.ACTIVE
            )

            val result = emergencyRepository.saveEmergencyRequest(request)
            _form.update {
                it.copy(
                    isLoading = false,
                    submittedRequest = if (result.isSuccess) result.getOrNull() else null,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
