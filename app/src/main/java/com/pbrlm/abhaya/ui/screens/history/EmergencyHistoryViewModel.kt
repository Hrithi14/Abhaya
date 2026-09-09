package com.pbrlm.abhaya.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.model.EmergencyStatus
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class HistoryFilter(val displayName: String) {
    ALL("All"), ACTIVE("Active"), RESOLVED("Resolved"),
    CANCELLED("Cancelled"), FALSE_REPORT("False Report")
}

data class HistoryUiState(
    val allRequests: List<EmergencyRequest> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val isLoading: Boolean = true
) {
    val filtered: List<EmergencyRequest> get() = when (filter) {
        HistoryFilter.ALL         -> allRequests
        HistoryFilter.ACTIVE      -> allRequests.filter { it.status == EmergencyStatus.ACTIVE || it.status == EmergencyStatus.ACKNOWLEDGED || it.status == EmergencyStatus.RESCUE_IN_PROGRESS }
        HistoryFilter.RESOLVED    -> allRequests.filter { it.status == EmergencyStatus.RESOLVED }
        HistoryFilter.CANCELLED   -> allRequests.filter { it.status == EmergencyStatus.CANCELLED }
        HistoryFilter.FALSE_REPORT-> allRequests.filter { it.status == EmergencyStatus.FALSE_REPORT }
    }
}

@HiltViewModel
class EmergencyHistoryViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        emergencyRepository.observeAllEmergencies(userProvider.getCurrentUserId()),
        _filter
    ) { list, filter ->
        HistoryUiState(allRequests = list, filter = filter, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun setFilter(filter: HistoryFilter) { _filter.value = filter }
}
