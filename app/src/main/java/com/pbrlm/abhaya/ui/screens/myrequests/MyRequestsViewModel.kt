package com.pbrlm.abhaya.ui.screens.myrequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MyRequestsUiState(
    val activeRequests: List<EmergencyRequest> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    val uiState: StateFlow<MyRequestsUiState> = emergencyRepository
        .observeActiveEmergencies(userProvider.getCurrentUserId())
        .map { list -> MyRequestsUiState(activeRequests = list, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyRequestsUiState()
        )
}
