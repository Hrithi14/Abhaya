package com.example.model

enum class HazardCategory(val displayName: String, val iconName: String) {
    FLOOD_WATER("FLOOD WATER", "flood"),
    WATERLOGGING("WATERLOGGING", "water"),
    FALLEN_TREE("FALLEN TREE", "nature"),
    ROADBLOCK("ROADBLOCK", "block"),
    POTHOLE("POTHOLE", "warning"),
    OPEN_WIRE("OPEN WIRE", "electric_bolt"),
    LANDSLIDE("LANDSLIDE", "terrain"),
    FIRE("FIRE", "local_fire_department"),
    MEDICAL_EMERGENCY("MEDICAL EMERGENCY", "medical_services")
}

data class HazardReport(
    val id: String,
    val category: HazardCategory,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val verified: Boolean = false,
    val upvotes: Int = 0,
    val waterDepth: String? = null,
    val imageUrl: String? = null,
    val userId: String = "user_mangaluru_101",
    val radiusMeters: Double = 150.0
)

data class EmergencyContact(
    val name: String,
    val type: String,
    val phone: String,
    val address: String,
    val distanceKm: Double,
    val note: String? = null
)

data class ShelterFacility(
    val name: String,
    val elevationMeters: Int,
    val capacity: Int,
    val currentOccupancy: Int,
    val address: String,
    val distanceKm: Double,
    val supplies: List<String>
)
