package com.pbrlm.abhaya.domain.model

import java.util.Date

enum class EscalationAgency(val displayName: String) {
    FIRE_AND_RESCUE("Fire and Rescue"),
    DISASTER_RESPONSE_FORCE("Disaster Response Force"),
    POLICE("Police"),
    MUNICIPAL_CONTROL_ROOM("Municipal Control Room");

    companion object {
        fun fromString(value: String): EscalationAgency =
            values().firstOrNull { it.name == value } ?: MUNICIPAL_CONTROL_ROOM
    }
}

enum class EscalationStatus(val displayName: String) {
    PENDING("Pending"),
    SENT("Sent"),
    ACKNOWLEDGED("Acknowledged"),
    RESOLVED("Resolved");

    companion object {
        fun fromString(value: String): EscalationStatus =
            values().firstOrNull { it.name == value } ?: PENDING
    }
}

data class Escalation(
    val escalationId: String = "",
    val emergencyRequestId: String = "",
    val escalationTime: Date = Date(),
    val agency: EscalationAgency = EscalationAgency.MUNICIPAL_CONTROL_ROOM,
    val priority: EmergencyPriority = EmergencyPriority.CRITICAL,
    val status: EscalationStatus = EscalationStatus.PENDING,
    val assignedResponder: String? = null,
    val responderContact: String? = null,
    val notes: String = ""
)
