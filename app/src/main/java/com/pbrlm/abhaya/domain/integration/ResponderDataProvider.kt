package com.pbrlm.abhaya.domain.integration

/**
 * Integration interface for MODULE 4 — Municipal Control Room (teammate module).
 *
 * The Emergency module exposes emergency data through this interface
 * so the control room module can read, acknowledge, and update status.
 */
interface ResponderDataProvider {
    /**
     * Called by the Municipal module when a responder acknowledges an emergency.
     */
    suspend fun acknowledgeEmergency(
        emergencyRequestId: String,
        responderId: String,
        responderContact: String
    ): Boolean

    /**
     * Called when a responder starts a rescue operation.
     */
    suspend fun startRescue(emergencyRequestId: String, responderId: String): Boolean

    /**
     * Called when an emergency is resolved by the responder.
     */
    suspend fun resolveEmergency(
        emergencyRequestId: String,
        responderId: String,
        notes: String
    ): Boolean

    /**
     * Returns responder-ready data for the control room dashboard.
     */
    suspend fun getResponderSummary(emergencyRequestId: String): ResponderSummary?
}

data class ResponderSummary(
    val emergencyRequestId: String,
    val emergencyType: String,
    val priority: String,
    val latitude: Double,
    val longitude: Double,
    val numberOfPeople: Int,
    val waterLevel: String?,
    val medicalRequired: Boolean,
    val photoUrl: String?,
    val timestamp: Long,
    val status: String,
    val assignedResponder: String?,
    val escalationRequired: Boolean
)
