package com.pbrlm.abhaya.data.repository

import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao
import com.pbrlm.abhaya.data.local.entity.toEntity
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.model.EmergencyStatus
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalEmergencyRepository @Inject constructor(
    private val dao: EmergencyRequestDao
) : EmergencyRepository {

    override suspend fun saveEmergencyRequest(request: EmergencyRequest): Result<EmergencyRequest> {
        return try {
            dao.insert(request.toEntity())
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEmergencyRequest(id: String): EmergencyRequest? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun updateStatus(id: String, status: EmergencyStatus): Result<Unit> {
        return try {
            dao.updateStatus(id, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelEmergency(id: String): Result<Unit> {
        return updateStatus(id, EmergencyStatus.CANCELLED)
    }

    suspend fun markEscalationRequired(id: String): Result<Unit> {
        return try {
            dao.markEscalationRequired(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeActiveEmergencies(userId: String): Flow<List<EmergencyRequest>> {
        return dao.observeActive(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun observeAllEmergencies(userId: String): Flow<List<EmergencyRequest>> {
        return dao.observeAll(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getActiveEmergencies(userId: String): List<EmergencyRequest> {
        return dao.getActive(userId).map { it.toDomain() }
    }

    override suspend fun getAllEmergencies(userId: String): List<EmergencyRequest> {
        return dao.getAll(userId).map { it.toDomain() }
    }

    override suspend fun getCriticalUnresolvedEmergencies(): List<EmergencyRequest> {
        return dao.getCriticalUnresolved().map { it.toDomain() }
    }
}
