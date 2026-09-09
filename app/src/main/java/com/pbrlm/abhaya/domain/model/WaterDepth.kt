package com.pbrlm.abhaya.domain.model

enum class WaterDepth(val displayName: String) {
    ANKLE("Ankle"),
    KNEE("Knee"),
    WAIST("Waist"),
    CHEST("Chest"),
    ABOVE_CHEST("Above Chest"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(value: String): WaterDepth =
            values().firstOrNull { it.name == value } ?: UNKNOWN
    }
}
