package com.pbrlm.abhaya.domain.model

enum class EmergencyStatus(val displayName: String) {
    ACTIVE("Active"),
    ACKNOWLEDGED("Acknowledged"),
    RESCUE_IN_PROGRESS("Rescue in Progress"),
    RESOLVED("Resolved"),
    CANCELLED("Cancelled"),
    FALSE_REPORT("False Report");

    val isTerminal: Boolean
        get() = this == RESOLVED || this == CANCELLED || this == FALSE_REPORT

    companion object {
        fun fromString(value: String): EmergencyStatus =
            values().firstOrNull { it.name == value } ?: ACTIVE
    }
}
