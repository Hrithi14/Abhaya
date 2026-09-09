package com.pbrlm.abhaya.domain.service

import com.pbrlm.abhaya.domain.model.EmergencyPriority
import com.pbrlm.abhaya.domain.model.EmergencyType
import com.pbrlm.abhaya.domain.model.MedicalEmergencyType
import com.pbrlm.abhaya.domain.model.WaterDepth

/**
 * Isolated, testable priority calculation service.
 * All priority logic lives here — never scattered across the UI or ViewModels.
 *
 * Rules:
 * CRITICAL: Immediate life threat — trapped, above-chest water, rising water, severe medical,
 *           unconscious, severe bleeding, multiple people in danger, vulnerable people in danger.
 * HIGH: Serious rescue needed — boat rescue, multiple people, water at chest/waist level.
 * MEDIUM: Shelter, non-urgent assistance.
 * LOW: Other non-critical requests.
 */
object PriorityCalculator {

    fun calculateForSos(): EmergencyPriority = EmergencyPriority.CRITICAL

    fun calculateForWaterRescue(
        waterDepth: WaterDepth,
        waterRising: Boolean?,
        peopleTrapped: Boolean,
        injured: Boolean,
        numberOfPeople: Int,
        children: Boolean,
        elderly: Boolean,
        disabled: Boolean
    ): EmergencyPriority {
        // Immediate CRITICAL conditions
        if (peopleTrapped) return EmergencyPriority.CRITICAL
        if (waterDepth == WaterDepth.ABOVE_CHEST) return EmergencyPriority.CRITICAL
        if (waterRising == true && waterDepth == WaterDepth.CHEST) return EmergencyPriority.CRITICAL
        if (waterRising == true && waterDepth == WaterDepth.WAIST && (children || elderly || disabled)) {
            return EmergencyPriority.CRITICAL
        }
        if (injured) return EmergencyPriority.CRITICAL
        if (numberOfPeople >= 5 && waterDepth == WaterDepth.CHEST) return EmergencyPriority.CRITICAL

        // HIGH conditions
        if (waterDepth == WaterDepth.CHEST) return EmergencyPriority.HIGH
        if (waterRising == true) return EmergencyPriority.HIGH
        if (numberOfPeople >= 3) return EmergencyPriority.HIGH
        if (children || elderly || disabled) return EmergencyPriority.HIGH
        if (waterDepth == WaterDepth.WAIST) return EmergencyPriority.HIGH

        // MEDIUM
        if (waterDepth == WaterDepth.KNEE || waterDepth == WaterDepth.ANKLE) {
            return EmergencyPriority.MEDIUM
        }

        return EmergencyPriority.HIGH // Default for water — always serious
    }

    fun calculateForMedical(
        medicalType: MedicalEmergencyType,
        personUnconscious: Boolean,
        breathingProblem: Boolean,
        severeBleeding: Boolean,
        pregnancyRelated: Boolean,
        injured: Boolean,
        numberOfPeople: Int,
        children: Boolean,
        elderly: Boolean,
        disabled: Boolean
    ): EmergencyPriority {
        // CRITICAL conditions
        if (personUnconscious) return EmergencyPriority.CRITICAL
        if (breathingProblem) return EmergencyPriority.CRITICAL
        if (severeBleeding) return EmergencyPriority.CRITICAL
        if (medicalType == MedicalEmergencyType.UNCONSCIOUS) return EmergencyPriority.CRITICAL
        if (medicalType == MedicalEmergencyType.BREATHING_PROBLEM) return EmergencyPriority.CRITICAL
        if (medicalType == MedicalEmergencyType.SEVERE_BLEEDING) return EmergencyPriority.CRITICAL
        if (numberOfPeople >= 3 && injured) return EmergencyPriority.CRITICAL

        // HIGH conditions
        if (pregnancyRelated || medicalType == MedicalEmergencyType.PREGNANCY) {
            return EmergencyPriority.HIGH
        }
        if (injured) return EmergencyPriority.HIGH
        if (children || elderly || disabled) return EmergencyPriority.HIGH
        if (numberOfPeople >= 3) return EmergencyPriority.HIGH

        // MEDIUM
        return EmergencyPriority.MEDIUM
    }

    fun calculateForBoatRescue(
        waterDepth: WaterDepth,
        waterRising: Boolean?,
        peopleTrapped: Boolean,
        hasmedicalEmergency: Boolean,
        numberOfPeople: Int,
        children: Boolean,
        elderly: Boolean,
        disabled: Boolean
    ): EmergencyPriority {
        if (peopleTrapped) return EmergencyPriority.CRITICAL
        if (hasmedicalEmergency) return EmergencyPriority.CRITICAL
        if (waterDepth == WaterDepth.ABOVE_CHEST) return EmergencyPriority.CRITICAL
        if (waterRising == true && (children || elderly || disabled)) return EmergencyPriority.CRITICAL

        if (waterDepth == WaterDepth.CHEST) return EmergencyPriority.HIGH
        if (waterRising == true) return EmergencyPriority.HIGH
        if (numberOfPeople >= 5) return EmergencyPriority.HIGH
        if (children || elderly || disabled) return EmergencyPriority.HIGH

        return EmergencyPriority.HIGH // Boat rescue is always at least HIGH
    }

    fun calculateForShelter(
        numberOfPeople: Int,
        children: Boolean,
        elderly: Boolean,
        disabled: Boolean
    ): EmergencyPriority {
        if (children || elderly || disabled) return EmergencyPriority.HIGH
        if (numberOfPeople >= 5) return EmergencyPriority.MEDIUM
        return EmergencyPriority.MEDIUM
    }

    /**
     * Determines if escalation is required.
     * Criteria: CRITICAL priority + ACTIVE status + unresolved for > 30 minutes.
     */
    fun isEscalationRequired(
        priority: EmergencyPriority,
        status: com.pbrlm.abhaya.domain.model.EmergencyStatus,
        createdAtMillis: Long,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (priority != EmergencyPriority.CRITICAL) return false
        if (status != com.pbrlm.abhaya.domain.model.EmergencyStatus.ACTIVE &&
            status != com.pbrlm.abhaya.domain.model.EmergencyStatus.ACKNOWLEDGED
        ) return false
        val elapsedMinutes = (currentTimeMillis - createdAtMillis) / 60_000
        return elapsedMinutes >= 30
    }
}
