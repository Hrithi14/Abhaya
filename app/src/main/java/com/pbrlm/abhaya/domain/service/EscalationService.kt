package com.pbrlm.abhaya.domain.service

import com.pbrlm.abhaya.domain.model.Escalation
import com.pbrlm.abhaya.domain.model.EscalationAgency
import com.pbrlm.abhaya.domain.model.EscalationStatus
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Escalation service — runs periodically and flags critical unresolved emergencies.
 *
 * Criteria: priority = CRITICAL + status = ACTIVE/ACKNOWLEDGED + age > 30 minutes
 *
 * This service does NOT contact government APIs directly.
 * It sets escalationRequired = true on the record so the Municipal module
 * (Module 4) can pick it up and dispatch accordingly.
 *
 * In production: replace the periodic loop with a WorkManager periodic task.
 */
@Singleton
class EscalationService @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) {
    companion object {
        const val ESCALATION_THRESHOLD_MINUTES = 30L
        const val CHECK_INTERVAL_MS = 5 * 60 * 1000L // check every 5 minutes
    }

    /**
     * Emits a list of escalations that were triggered this cycle.
     * Collect this flow in a ViewModel or background scope.
     */
    fun observeEscalations(): Flow<List<Escalation>> = flow {
        while (true) {
            val escalations = checkAndEscalate()
            if (escalations.isNotEmpty()) emit(escalations)
            delay(CHECK_INTERVAL_MS)
        }
    }

    /**
     * Checks all critical unresolved emergencies and marks those
     * that have been waiting more than 30 minutes.
     */
    suspend fun checkAndEscalate(): List<Escalation> {
        val critical = emergencyRepository.getCriticalUnresolvedEmergencies()
        val now = System.currentTimeMillis()
        val escalations = mutableListOf<Escalation>()

        critical.forEach { request ->
            val shouldEscalate = PriorityCalculator.isEscalationRequired(
                priority          = request.priority,
                status            = request.status,
                createdAtMillis   = request.timestamp.time,
                currentTimeMillis = now
            )

            if (shouldEscalate && !request.escalationRequired) {
                // Mark escalation on the record
                emergencyRepository.updateStatus(
                    request.emergencyRequestId,
                    request.status  // keep status, just flag escalation
                )

                escalations.add(
                    Escalation(
                        escalationId       = EmergencyIdGenerator.generateEscalationId(),
                        emergencyRequestId = request.emergencyRequestId,
                        escalationTime     = Date(now),
                        agency             = determineAgency(request.emergencyType.name),
                        priority           = request.priority,
                        status             = EscalationStatus.PENDING,
                        notes              = "Auto-escalated: unresolved for >${ESCALATION_THRESHOLD_MINUTES} minutes"
                    )
                )
            }
        }

        return escalations
    }

    private fun determineAgency(emergencyType: String): EscalationAgency {
        return when (emergencyType) {
            "WATER_RESCUE", "BOAT_RESCUE" -> EscalationAgency.FIRE_AND_RESCUE
            "MEDICAL"                     -> EscalationAgency.FIRE_AND_RESCUE
            "SOS"                         -> EscalationAgency.DISASTER_RESPONSE_FORCE
            else                          -> EscalationAgency.MUNICIPAL_CONTROL_ROOM
        }
    }
}
