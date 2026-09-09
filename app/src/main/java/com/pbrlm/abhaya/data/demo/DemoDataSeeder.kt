package com.pbrlm.abhaya.data.demo

import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao
import com.pbrlm.abhaya.data.local.entity.toEntity
import com.pbrlm.abhaya.domain.model.*
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds clearly-labelled demo data for college presentation.
 *
 * IMPORTANT: Demo records have isDemoData = true and IDs beginning with "ER-DEMO-".
 * They are NEVER submitted to any real backend.
 * They do NOT appear in the Community Help screen (filtered by isDemoData).
 *
 * Call seed() once on first app launch in debug mode.
 */
@Singleton
class DemoDataSeeder @Inject constructor(
    private val dao: EmergencyRequestDao
) {
    suspend fun seed(userId: String) {
        val demoRequests = listOf(
            // ER-DEMO-001: Water Rescue — CRITICAL
            EmergencyRequest(
                emergencyRequestId = "ER-DEMO-001",
                userId             = userId,
                emergencyType      = EmergencyType.WATER_RESCUE,
                latitude           = 12.9123,
                longitude          = 74.8512,
                gpsAccuracy        = 5f,
                timestamp          = Date(System.currentTimeMillis() - 45 * 60 * 1000L),
                numberOfPeople     = 4,
                waterDepth         = WaterDepth.CHEST,
                waterRising        = true,
                peopleTrapped      = true,
                children           = true,
                contactNumber      = "9876543210",
                description        = "Family trapped on 1st floor. Water rising fast.",
                priority           = EmergencyPriority.CRITICAL,
                status             = EmergencyStatus.ACTIVE,
                escalationRequired = true,
                isDemoData         = true
            ),
            // ER-DEMO-002: Medical — HIGH
            EmergencyRequest(
                emergencyRequestId   = "ER-DEMO-002",
                userId               = userId,
                emergencyType        = EmergencyType.MEDICAL,
                latitude             = 12.9135,
                longitude            = 74.8498,
                gpsAccuracy          = 8f,
                timestamp            = Date(System.currentTimeMillis() - 20 * 60 * 1000L),
                numberOfPeople       = 1,
                medicalEmergencyType = MedicalEmergencyType.INJURY,
                injured              = true,
                elderly              = true,
                contactNumber        = "9876500001",
                description          = "Elderly person injured during evacuation.",
                priority             = EmergencyPriority.HIGH,
                status               = EmergencyStatus.ACKNOWLEDGED,
                isDemoData           = true
            ),
            // ER-DEMO-003: Shelter — MEDIUM, RESOLVED
            EmergencyRequest(
                emergencyRequestId = "ER-DEMO-003",
                userId             = userId,
                emergencyType      = EmergencyType.SHELTER,
                latitude           = 12.9100,
                longitude          = 74.8550,
                gpsAccuracy        = 10f,
                timestamp          = Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000L),
                numberOfPeople     = 6,
                children           = true,
                contactNumber      = "9800001234",
                description        = "Family of 6 displaced, need temporary shelter.",
                priority           = EmergencyPriority.MEDIUM,
                status             = EmergencyStatus.RESOLVED,
                responderNotes     = "Accommodated at Govt. HS School relief center.",
                isDemoData         = true
            ),
            // ER-DEMO-004: Boat Rescue — HIGH, Rescue in Progress
            EmergencyRequest(
                emergencyRequestId = "ER-DEMO-004",
                userId             = userId,
                emergencyType      = EmergencyType.BOAT_RESCUE,
                latitude           = 12.9150,
                longitude          = 74.8480,
                gpsAccuracy        = 6f,
                timestamp          = Date(System.currentTimeMillis() - 35 * 60 * 1000L),
                numberOfPeople     = 3,
                waterDepth         = WaterDepth.WAIST,
                waterRising        = false,
                disabled           = true,
                contactNumber      = "9900012345",
                description        = "3 people stranded. One wheelchair user.",
                priority           = EmergencyPriority.HIGH,
                status             = EmergencyStatus.RESCUE_IN_PROGRESS,
                assignedResponderId= "RESP-001",
                responderContact   = "9911000001",
                isDemoData         = true
            ),
            // ER-DEMO-005: SOS — CRITICAL, Active
            EmergencyRequest(
                emergencyRequestId = "ER-DEMO-005",
                userId             = userId,
                emergencyType      = EmergencyType.SOS,
                latitude           = 12.9112,
                longitude          = 74.8520,
                gpsAccuracy        = 4f,
                timestamp          = Date(System.currentTimeMillis() - 10 * 60 * 1000L),
                numberOfPeople     = 1,
                priority           = EmergencyPriority.CRITICAL,
                status             = EmergencyStatus.ACTIVE,
                isDemoData         = true
            )
        )

        demoRequests.forEach { request ->
            // Only insert if not already present (idempotent)
            if (dao.getById(request.emergencyRequestId) == null) {
                dao.insert(request.toEntity())
            }
        }
    }
}
