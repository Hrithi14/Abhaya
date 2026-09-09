package com.pbrlm.abhaya.domain.model

enum class EmergencyType(val displayName: String, val emoji: String) {
    SOS("SOS", "🚨"),
    WATER_RESCUE("Trapped by Water", "🌊"),
    MEDICAL("Medical Emergency", "🏥"),
    BOAT_RESCUE("Boat Rescue", "🚤"),
    SHELTER("Shelter Request", "🏠"),
    OTHER("Other", "⚠️");

    companion object {
        fun fromString(value: String): EmergencyType =
            values().firstOrNull { it.name == value } ?: OTHER
    }
}
