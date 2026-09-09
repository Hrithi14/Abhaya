package com.pbrlm.abhaya.data.local.dao

import androidx.room.*
import com.pbrlm.abhaya.data.local.entity.HelpOfferEntity
import com.pbrlm.abhaya.data.local.entity.VolunteerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(entity: VolunteerEntity)

    @Query("UPDATE volunteers SET availableToHelp = :available WHERE volunteerId = :id")
    suspend fun updateAvailability(id: String, available: Boolean)

    @Query("SELECT * FROM volunteers WHERE availableToHelp = 1")
    fun observeAvailable(): Flow<List<VolunteerEntity>>

    @Query("SELECT * FROM volunteers WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): VolunteerEntity?
}

@Dao
interface HelpOfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HelpOfferEntity)

    @Query("UPDATE help_offers SET status = :status WHERE helpOfferId = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT * FROM help_offers WHERE emergencyRequestId = :emergencyId ORDER BY timestampMillis DESC")
    fun observeForEmergency(emergencyId: String): Flow<List<HelpOfferEntity>>

    @Query("SELECT * FROM help_offers WHERE userId = :userId ORDER BY timestampMillis DESC")
    fun observeMyOffers(userId: String): Flow<List<HelpOfferEntity>>

    @Query("SELECT * FROM help_offers WHERE emergencyRequestId = :emergencyId ORDER BY timestampMillis DESC")
    suspend fun getForEmergency(emergencyId: String): List<HelpOfferEntity>
}
