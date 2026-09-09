package com.pbrlm.abhaya.domain.model

data class Volunteer(
    val volunteerId: String = "",
    val userId: String = "",
    val name: String = "",
    val contactNumber: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val safeZoneStatus: Boolean = true,
    val availableToHelp: Boolean = false,
    val helpType: HelpType = HelpType.OTHER,
    val capacity: Int = 1,
    val notes: String = ""
)

enum class HelpType(val displayName: String, val emoji: String) {
    BOAT("Boat", "🚤"),
    OFF_ROAD_VEHICLE("Off-Road Vehicle", "🚙"),
    FOOD("Food", "🍲"),
    DRINKING_WATER("Drinking Water", "💧"),
    SHELTER("Shelter", "🏠"),
    ROPE("Rope", "🪢"),
    FIRST_AID("First Aid", "🩺"),
    TRANSPORT("Transport", "🚗"),
    OTHER("Other", "🤝");

    companion object {
        fun fromString(value: String): HelpType =
            values().firstOrNull { it.name == value } ?: OTHER
    }
}
