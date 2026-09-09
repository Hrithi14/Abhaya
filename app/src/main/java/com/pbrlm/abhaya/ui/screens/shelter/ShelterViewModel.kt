package com.pbrlm.abhaya.ui.screens.shelter

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

data class ShelterFormState(
    val numberOfPeople: Int = 1,
    val children: Boolean = false,
    val elderly: Boolean = false,
    val disabled: Boolean = false,
    val contactNumber: String = "",
    val currentSituation: String = "",
    val additionalInfo: String = "",
    val contactError: String = "",
    val isLoading: Boolean = false,
    val submittedRequest: EmergencyRequest? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ShelterViewModel @Inject constructor(
    private val locationService: LocationService,
    private val emergencyRepository: EmergencyRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _form = MutableStateFlow(ShelterFormState())
    val form: StateFlow<ShelterFormState> = _form.asStateFlow()

    fun updatePeople(n: Int)          { _form.update { it.copy(numberOfPeople = n) } }
    fun updateChildren(v: Boolean)    { _form.update { it.copy(children = v) } }
    fun updateElderly(v: Boolean)     { _form.update { it.copy(elderly = v) } }
    fun updateDisabled(v: Boolean)    { _form.update { it.copy(disabled = v) } }
    fun updateContact(s: String)      { _form.update { it.copy(contactNumber = s, contactError = "") } }
    fun updateSituation(s: String)    { _form.update { it.copy(currentSituation = s) } }
    fun updateInfo(s: String)         { _form.update { it.copy(additionalInfo = s) } }
    fun clearSubmission()             { _form.update { it.copy(submittedRequest = null, errorMessage = null) } }

    fun submit() {
        val f = _form.value
        if (f.contactNumber.isNotBlank() && !f.contactNumber.matches(Regex("\\d{10,12}"))) {
            _form.update { it.copy(contactError = "Enter a valid phone number") }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(isLoading = true) }
            val location = locationService.getCurrentLocation()
            val priority = PriorityCalculator.calculateForShelter(
                numberOfPeople = f.numberOfPeople,
                children       = f.children,
                elderly        = f.elderly,
                disabled       = f.disabled
            )
            val request = EmergencyRequest(
                emergencyRequestId = EmergencyIdGenerator.generate(),
                userId             = userProvider.getCurrentUserId(),
                emergencyType      = EmergencyType.SHELTER,
                latitude           = location.latitude,
                longitude          = location.longitude,
                gpsAccuracy        = location.accuracy,
                timestamp          = Date(),
                numberOfPeople     = f.numberOfPeople,
                children           = f.children,
                elderly            = f.elderly,
                disabled           = f.disabled,
                contactNumber      = f.contactNumber,
                description        = buildString {
                    if (f.currentSituation.isNotBlank()) append("Situation: ${f.currentSituation}\n")
                    if (f.additionalInfo.isNotBlank()) append(f.additionalInfo)
                },
                priority           = priority,
                status             = EmergencyStatus.ACTIVE
            )
            val result = emergencyRepository.saveEmergencyRequest(request)
            _form.update {
                it.copy(
                    isLoading = false,
                    submittedRequest = result.getOrNull(),
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
