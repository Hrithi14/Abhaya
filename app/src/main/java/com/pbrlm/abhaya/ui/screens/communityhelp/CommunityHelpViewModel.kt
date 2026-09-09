package com.pbrlm.abhaya.ui.screens.communityhelp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.model.*
import com.pbrlm.abhaya.domain.repository.HelpOfferRepository
import com.pbrlm.abhaya.domain.service.EmergencyIdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class CommunityHelpUiState(
    val emergencies: List<EmergencyRequest> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CommunityHelpViewModel @Inject constructor(
    // DAO injected directly here because the community feed queries ALL users,
    // not just the current user — it is a read-only feed query.
    private val emergencyRequestDao: EmergencyRequestDao,
    private val helpOfferRepository: HelpOfferRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    val uiState: StateFlow<CommunityHelpUiState> = emergencyRequestDao
        .observeEmergenciesForCommunityHelp()
        .map { list ->
            CommunityHelpUiState(
                emergencies = list.map { it.toDomain() },
                isLoading   = false
            )
        }
        .stateIn(
            scope   = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityHelpUiState()
        )

    fun offerHelp(emergencyRequestId: String, helpType: HelpType, message: String) {
        viewModelScope.launch {
            val offer = HelpOffer(
                helpOfferId        = EmergencyIdGenerator.generateHelpOfferId(),
                emergencyRequestId = emergencyRequestId,
                volunteerId        = userProvider.getCurrentUserId(),
                userId             = userProvider.getCurrentUserId(),
                helpType           = helpType,
                message            = message,
                timestamp          = Date(),
                status             = HelpOfferStatus.OFFERED
            )
            helpOfferRepository.createHelpOffer(offer)
        }
    }
}
