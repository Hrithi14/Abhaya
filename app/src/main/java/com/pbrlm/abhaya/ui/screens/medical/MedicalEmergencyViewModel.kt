package com.pbrlm.abhaya.ui.screens.medical

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

data class MedicalFormState(
    val medicalType: MedicalEmergencyType = MedicalEmergencyType.OTHER,
    val numberOfPeople: Int = 1,
    val personUnconscious: Boolean = false,
    val breathingProblem: Boolean = false,
    val severeBleeding: Boolean = false,
    val pregnancyRelated: Boolean = false,
    val injured: Boolean = false,
    val children: Boolean = false,
    val elderly: Boolean = false,
    val disabled: Boolean = false,
    val contactNumber: String = "",
    val description: String = "",
    val photoUri: Uri? = null,
    val contactError: String = "",
    val isLoading: Boolean = false,
    val submittedRequest: EmergencyRequest? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class MedicalEmergencyViewModel @Inject constructor(
    private val locationService: LocationService,
    private val emergencyRepository: EmergencyRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _form = MutableStateFlow(MedicalFormState())
    val form: StateFlow<MedicalFormState> = _form.asStateFlow()

    fun updateMedicalType(t: MedicalEmergencyType) { _form.update { it.copy(medicalType = t) } }
    fun updateNumberOfPeople(n: Int)               { _form.update { it.copy(numberOfPeople = n) } }
    fun updateUnconscious(v: Boolean)              { _form.update { it.copy(personUnconscious = v) } }
    fun updateBreathing(v: Boolean)                { _form.update { it.copy(breathingProblem = v) } }
    fun updateSevereBleeding(v: Boolean)           { _form.update { it.copy(severeBleeding = v) } }
    fun updatePregnancy(v: Boolean)                { _form.update { it.copy(pregnancyRelated = v) } }
    fun updateInjured(v: Boolean)                  { _form.update { it.copy(injured = v) } }
    fun updateChildren(v: Boolean)                 { _form.update { it.copy(children = v) } }
    fun updateElderly(v: Boolean)                  { _form.update { it.copy(elderly = v) } }
    fun updateDisabled(v: Boolean)                 { _form.update { it.copy(disabled = v) } }
    fun updateContact(s: String)                   { _form.update { it.copy(contactNumber = s, contactError = "") } }
    fun updateDescription(s: String)               { _form.update { it.copy(description = s) } }
    fun updatePhoto(uri: Uri?)                     { _form.update { it.copy(photoUri = uri) } }
    fun clearSubmission()                          { _form.update { it.copy(submittedRequest = null, errorMessage = null) } }

    fun submit() {
        val f = _form.value
        if (f.contactNumber.isNotBlank() && !f.contactNumber.matches(Regex("\\d{10,12}"))) {
            _form.update { it.copy(contactError = "Enter a valid phone number") }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(isLoading = true) }
            val location = locationService.getCurrentLocation()
            val priority = PriorityCalculator.calculateForMedical(
                medicalType      = f.medicalType,
                personUnconscious= f.personUnconscious,
                breathingProblem = f.breathingProblem,
                severeBleeding   = f.severeBleeding,
                pregnancyRelated = f.pregnancyRelated,
                injured          = f.injured,
                numberOfPeople   = f.numberOfPeople,
                children         = f.children,
                elderly          = f.elderly,
                disabled         = f.disabled
            )
            val request = EmergencyRequest(
                emergencyRequestId   = EmergencyIdGenerator.generate(),
                userId               = userProvider.getCurrentUserId(),
                emergencyType        = EmergencyType.MEDICAL,
                latitude             = location.latitude,
                longitude            = location.longitude,
                gpsAccuracy          = location.accuracy,
                timestamp            = Date(),
                numberOfPeople       = f.numberOfPeople,
                medicalEmergencyType = f.medicalType,
                personUnconscious    = f.personUnconscious,
                breathingProblem     = f.breathingProblem,
                severeBleeding       = f.severeBleeding,
                pregnancyRelated     = f.pregnancyRelated,
                injured              = f.injured,
                medicalRequired      = true,
                children             = f.children,
                elderly              = f.elderly,
                disabled             = f.disabled,
                contactNumber        = f.contactNumber,
                description          = f.description,
                photoUrl             = f.photoUri?.toString(),
                priority             = priority,
                status               = EmergencyStatus.ACTIVE
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
