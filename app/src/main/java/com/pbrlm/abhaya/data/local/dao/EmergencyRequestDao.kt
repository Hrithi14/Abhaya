package com.pbrlm.abhaya.data.local.dao

import androidx.room.*
import com.pbrlm.abhaya.data.local.entity.EmergencyRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EmergencyRequestEntity)

    @Update
    suspend fun update(entity: EmergencyRequestEntity)

    @Query("SELECT * FROM emergency_requests WHERE emergencyRequestId = :id")
    suspend fun getById(id: String): EmergencyRequestEntity?

    @Query("SELECT * FROM emergency_requests WHERE userId = :userId AND status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') ORDER BY timestampMillis DESC")
    fun observeActive(userId: String): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE userId = :userId ORDER BY timestampMillis DESC")
    fun observeAll(userId: String): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE userId = :userId AND status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') ORDER BY timestampMillis DESC")
    suspend fun getActive(userId: String): List<EmergencyRequestEntity>

    @Query("SELECT * FROM emergency_requests WHERE userId = :userId ORDER BY timestampMillis DESC")
    suspend fun getAll(userId: String): List<EmergencyRequestEntity>

    @Query("UPDATE emergency_requests SET status = :status WHERE emergencyRequestId = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE emergency_requests SET escalationRequired = 1 WHERE emergencyRequestId = :id")
    suspend fun markEscalationRequired(id: String)

    @Query("SELECT * FROM emergency_requests WHERE priority = 'CRITICAL' AND status IN ('ACTIVE','ACKNOWLEDGED') ORDER BY timestampMillis ASC")
    suspend fun getCriticalUnresolved(): List<EmergencyRequestEntity>

    // Community help: active emergencies eligible for community assistance (all users)
    @Query("SELECT * FROM emergency_requests WHERE status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') AND isDemoData = 0 ORDER BY timestampMillis DESC LIMIT 50")
    fun observeEmergenciesForCommunityHelp(): Flow<List<EmergencyRequestEntity>>

    @Query("DELETE FROM emergency_requests WHERE emergencyRequestId = :id")
    suspend fun delete(id: String)
}
