package com.pbrlm.abhaya.domain.repository

import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.model.EmergencyStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for emergency requests.
 * The UI layer depends on this interface — never on a concrete implementation.
 *
 * Implementations:
 * - LocalEmergencyRepository (Room DB — available now)
 * - FirebaseEmergencyRepository (Firebase Firestore — injectable later)
 */
interface EmergencyRepository {
    suspend fun saveEmergencyRequest(request: EmergencyRequest): Result<EmergencyRequest>
    suspend fun getEmergencyRequest(id: String): EmergencyRequest?
    suspend fun updateStatus(id: String, status: EmergencyStatus): Result<Unit>
    suspend fun cancelEmergency(id: String): Result<Unit>
    fun observeActiveEmergencies(userId: String): Flow<List<EmergencyRequest>>
    fun observeAllEmergencies(userId: String): Flow<List<EmergencyRequest>>
    suspend fun getActiveEmergencies(userId: String): List<EmergencyRequest>
    suspend fun getAllEmergencies(userId: String): List<EmergencyRequest>
    suspend fun getCriticalUnresolvedEmergencies(): List<EmergencyRequest>
}
