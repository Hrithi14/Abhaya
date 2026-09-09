package com.pbrlm.abhaya.domain.repository

import com.pbrlm.abhaya.domain.model.Volunteer
import kotlinx.coroutines.flow.Flow

interface VolunteerRepository {
    suspend fun registerVolunteer(volunteer: Volunteer): Result<Volunteer>
    suspend fun updateAvailability(volunteerId: String, available: Boolean): Result<Unit>
    fun observeNearbyVolunteers(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Flow<List<Volunteer>>
    suspend fun getVolunteerByUserId(userId: String): Volunteer?
}
