package com.pbrlm.abhaya.domain.model

enum class EmergencyPriority(val displayName: String, val level: Int) {
    CRITICAL("Critical", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1);

    companion object {
        fun fromString(value: String): EmergencyPriority =
            values().firstOrNull { it.name == value } ?: LOW
    }
}
