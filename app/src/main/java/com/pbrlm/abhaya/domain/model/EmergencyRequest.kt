package com.pbrlm.abhaya.domain.model

import java.util.Date

/**
 * Core domain model for an emergency request.
 * Contains all fields needed for SOS, water rescue, medical, boat rescue, shelter.
 *
 * hazardId, clusterId, zoneId are integration points for teammates' modules.
 * assignedResponderId and responder fields are integration points for the Municipal module.
 */
data class EmergencyRequest(
    val emergencyRequestId: String = "",
    val userId: String = "",

    // Integration fields — populated by teammate modules later
    val hazardId: String? = null,
    val clusterId: String? = null,
    val zoneId: String? = null,

    val emergencyType: EmergencyType = EmergencyType.SOS,

    // Location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val gpsAccuracy: Float = 0f,
    val timestamp: Date = Date(),

    // People details
    val numberOfPeople: Int = 1,
    val children: Boolean = false,
    val elderly: Boolean = false,
    val disabled: Boolean = false,
    val peopleTrapped: Boolean = false,
    val injured: Boolean = false,
    val medicalRequired: Boolean = false,

    // Water-specific
    val waterDepth: WaterDepth? = null,
    val waterLevel: String? = null,
    val waterRising: Boolean? = null,

    // Medical-specific
    val medicalEmergencyType: MedicalEmergencyType? = null,
    val personUnconscious: Boolean = false,
    val breathingProblem: Boolean = false,
    val severeBleeding: Boolean = false,
    val pregnancyRelated: Boolean = false,

    // General
    val description: String = "",
    val buildingFloor: String? = null,
    val contactNumber: String = "",
    val photoUrl: String? = null,

    // Status and priority
    val priority: EmergencyPriority = EmergencyPriority.MEDIUM,
    val status: EmergencyStatus = EmergencyStatus.ACTIVE,

    // Responder integration — populated by Municipal module
    val assignedResponderId: String? = null,
    val responderContact: String? = null,
    val responseTime: Date? = null,
    val resolutionTime: Date? = null,
    val responderNotes: String? = null,

    val escalationRequired: Boolean = false,

    // Demo flag — demo requests will never be sent to real backend
    val isDemoData: Boolean = false
)
