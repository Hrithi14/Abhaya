package com.pbrlm.abhaya.domain.repository

import com.pbrlm.abhaya.domain.model.HelpOffer
import com.pbrlm.abhaya.domain.model.HelpOfferStatus
import kotlinx.coroutines.flow.Flow

interface HelpOfferRepository {
    suspend fun createHelpOffer(offer: HelpOffer): Result<HelpOffer>
    suspend fun updateHelpOfferStatus(offerId: String, status: HelpOfferStatus): Result<Unit>
    fun observeOffersForEmergency(emergencyRequestId: String): Flow<List<HelpOffer>>
    fun observeMyOffers(userId: String): Flow<List<HelpOffer>>
    suspend fun getOffersForEmergency(emergencyRequestId: String): List<HelpOffer>
}
