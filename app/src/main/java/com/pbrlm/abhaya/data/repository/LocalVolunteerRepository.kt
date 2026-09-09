package com.pbrlm.abhaya.data.repository

import com.pbrlm.abhaya.data.local.dao.HelpOfferDao
import com.pbrlm.abhaya.data.local.dao.VolunteerDao
import com.pbrlm.abhaya.data.local.entity.toEntity
import com.pbrlm.abhaya.domain.model.HelpOffer
import com.pbrlm.abhaya.domain.model.HelpOfferStatus
import com.pbrlm.abhaya.domain.model.Volunteer
import com.pbrlm.abhaya.domain.repository.HelpOfferRepository
import com.pbrlm.abhaya.domain.repository.VolunteerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalVolunteerRepository @Inject constructor(
    private val volunteerDao: VolunteerDao
) : VolunteerRepository {

    override suspend fun registerVolunteer(volunteer: Volunteer): Result<Volunteer> {
        return try {
            volunteerDao.insertVolunteer(volunteer.toEntity())
            Result.success(volunteer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAvailability(volunteerId: String, available: Boolean): Result<Unit> {
        return try {
            volunteerDao.updateAvailability(volunteerId, available)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNearbyVolunteers(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Flow<List<Volunteer>> {
        // Local impl returns all available volunteers.
        // Full geo-query handled in Firebase implementation.
        return volunteerDao.observeAvailable().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getVolunteerByUserId(userId: String): Volunteer? {
        return volunteerDao.getByUserId(userId)?.toDomain()
    }
}

@Singleton
class LocalHelpOfferRepository @Inject constructor(
    private val helpOfferDao: HelpOfferDao
) : HelpOfferRepository {

    override suspend fun createHelpOffer(offer: HelpOffer): Result<HelpOffer> {
        return try {
            helpOfferDao.insert(offer.toEntity())
            Result.success(offer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHelpOfferStatus(offerId: String, status: HelpOfferStatus): Result<Unit> {
        return try {
            helpOfferDao.updateStatus(offerId, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeOffersForEmergency(emergencyRequestId: String): Flow<List<HelpOffer>> {
        return helpOfferDao.observeForEmergency(emergencyRequestId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeMyOffers(userId: String): Flow<List<HelpOffer>> {
        return helpOfferDao.observeMyOffers(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getOffersForEmergency(emergencyRequestId: String): List<HelpOffer> {
        return helpOfferDao.getForEmergency(emergencyRequestId).map { it.toDomain() }
    }
}
