package com.pbrlm.abhaya.domain.model

enum class MedicalEmergencyType(val displayName: String) {
    INJURY("Injury"),
    UNCONSCIOUS("Unconscious"),
    BREATHING_PROBLEM("Breathing Problem"),
    SEVERE_BLEEDING("Severe Bleeding"),
    PREGNANCY("Pregnancy Related"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): MedicalEmergencyType =
            values().firstOrNull { it.name == value } ?: OTHER
    }
}
