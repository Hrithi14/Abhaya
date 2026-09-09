package com.example.data

import com.example.model.EmergencyContact
import com.example.model.HazardCategory
import com.example.model.HazardReport
import com.example.model.ShelterFacility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object HazardRepository {
    // Default location: Mangaluru, Karnataka (Lat: 12.9141, Lon: 74.8560)
    const val DEFAULT_LATITUDE = 12.9141
    const val DEFAULT_LONGITUDE = 74.8560

    private val initialReports = listOf(
        HazardReport(
            id = "rep_001",
            category = HazardCategory.FLOOD_WATER,
            description = "Knee-deep flood water near MG Road junction. Traffic diverted towards KS Rao Road.",
            latitude = 12.9148,
            longitude = 74.8552,
            createdAt = System.currentTimeMillis() - (2 * 60 * 1000), // 2 mins ago
            verified = true,
            upvotes = 14,
            waterDepth = "2.4 ft",
            radiusMeters = 220.0
        ),
        HazardReport(
            id = "rep_002",
            category = HazardCategory.WATERLOGGING,
            description = "Heavy waterlogging under Pumpwell Flyover. Two wheelers stranded.",
            latitude = 12.8715,
            longitude = 74.8698,
            createdAt = System.currentTimeMillis() - (7 * 60 * 1000), // 7 mins ago
            verified = true,
            upvotes = 28,
            waterDepth = "1.8 ft",
            radiusMeters = 180.0
        ),
        HazardReport(
            id = "rep_003",
            category = HazardCategory.FALLEN_TREE,
            description = "Large banyan tree branch blocking left lane entirely near Ladyhill Circle.",
            latitude = 12.8942,
            longitude = 74.8385,
            createdAt = System.currentTimeMillis() - (10 * 60 * 1000), // 10 mins ago
            verified = false,
            upvotes = 5,
            radiusMeters = 50.0
        ),
        HazardReport(
            id = "rep_004",
            category = HazardCategory.OPEN_WIRE,
            description = "Snapping live electric wire submerged in puddle outside Kadri Park gate.",
            latitude = 12.8830,
            longitude = 74.8580,
            createdAt = System.currentTimeMillis() - (15 * 60 * 1000),
            verified = true,
            upvotes = 42,
            radiusMeters = 80.0
        ),
        HazardReport(
            id = "rep_005",
            category = HazardCategory.LANDSLIDE,
            description = "Soil slipping on mud embankment along Gurupura stretch. Precaution advised.",
            latitude = 12.9320,
            longitude = 74.9100,
            createdAt = System.currentTimeMillis() - (35 * 60 * 1000),
            verified = false,
            upvotes = 9,
            radiusMeters = 120.0
        ),
        HazardReport(
            id = "rep_006",
            category = HazardCategory.ROADBLOCK,
            description = "Police barricades installed on Kottara Chowki underpass due to flash surge.",
            latitude = 12.9064,
            longitude = 74.8390,
            createdAt = System.currentTimeMillis() - (45 * 60 * 1000),
            verified = true,
            upvotes = 19,
            radiusMeters = 90.0
        )
    )

    private val _reports = MutableStateFlow<List<HazardReport>>(initialReports)
    val reports: StateFlow<List<HazardReport>> = _reports.asStateFlow()

    fun addReport(
        category: HazardCategory,
        description: String,
        latitude: Double,
        longitude: Double,
        waterDepth: String?,
        imageUrl: String?
    ): HazardReport {
        val newReport = HazardReport(
            id = "rep_" + UUID.randomUUID().toString().substring(0, 8),
            category = category,
            description = description,
            latitude = latitude,
            longitude = longitude,
            createdAt = System.currentTimeMillis(),
            verified = false,
            upvotes = 1,
            waterDepth = waterDepth?.ifBlank { null },
            imageUrl = imageUrl,
            radiusMeters = if (category == HazardCategory.FLOOD_WATER || category == HazardCategory.WATERLOGGING) 160.0 else 60.0
        )
        _reports.value = listOf(newReport) + _reports.value
        return newReport
    }

    fun upvoteReport(id: String) {
        _reports.value = _reports.value.map {
            if (it.id == id) it.copy(upvotes = it.upvotes + 1) else it
        }
    }

    fun toggleVerification(id: String) {
        _reports.value = _reports.value.map {
            if (it.id == id) it.copy(verified = !it.verified) else it
        }
    }

    fun deleteReport(id: String) {
        _reports.value = _reports.value.filter { it.id != id }
    }

    fun getDynamicAISummary(): String {
        val count = _reports.value.size
        val floodCount = _reports.value.count {
            it.category == HazardCategory.FLOOD_WATER || it.category == HazardCategory.WATERLOGGING
        }
        val topHazard = _reports.value.firstOrNull()
        return if (topHazard != null) {
            "Active flood alerts: $floodCount areas waterlogged. High risk at ${topHazard.category.displayName}. Safe evacuation path open towards High Ground Town Hall Shelter."
        } else {
            "Situation normal in Mangaluru perimeter. Safe path open towards High Ground Shelter."
        }
    }

    val emergencyContacts = listOf(
        EmergencyContact(
            name = "National Disaster Helpline",
            type = "Emergency Response",
            phone = "112",
            address = "Central Control Room, 24/7 Operations",
            distanceKm = 0.0,
            note = "Immediate dispatch for police, fire & medical"
        ),
        EmergencyContact(
            name = "Wenlock District Hospital",
            type = "HOSPITAL",
            phone = "+91 824 242 4555",
            address = "Hampankatta, Mangaluru, Karnataka 575001",
            distanceKm = 1.2,
            note = "Trauma center & 24/7 ICU ambulance bay"
        ),
        EmergencyContact(
            name = "KMC Hospital Ambedkar Circle",
            type = "HOSPITAL",
            phone = "+91 824 244 4590",
            address = "Dr. B R Ambedkar Circle, Balmatta, Mangaluru",
            distanceKm = 1.8,
            note = "Emergency rescue & critical water-borne injury care"
        ),
        EmergencyContact(
            name = "Mangalore North Police Station (Bunder)",
            type = "POLICE",
            phone = "+91 824 222 0524",
            address = "Bunder, Mangaluru, Karnataka 575001",
            distanceKm = 1.5,
            note = "Coastal & flood relief coordination unit"
        ),
        EmergencyContact(
            name = "Kadri Police Station",
            type = "POLICE",
            phone = "+91 824 222 0528",
            address = "Kadri Hills, Mangaluru, Karnataka 575002",
            distanceKm = 2.4,
            note = "High ground patrol squad"
        )
    )

    val safeShelters = listOf(
        ShelterFacility(
            name = "Town Hall Mangaluru (High Ground Relief)",
            elevationMeters = 42,
            capacity = 850,
            currentOccupancy = 210,
            address = "Nehru Maidan Rd, Hampankatta, Mangaluru",
            distanceKm = 0.9,
            supplies = listOf("Clean Water", "Hot Food", "First Aid", "Backup Generators")
        ),
        ShelterFacility(
            name = "St. Aloysius High-Ground Community Center",
            elevationMeters = 58,
            capacity = 1200,
            currentOccupancy = 440,
            address = "Light House Hill Rd, Mangaluru 575003",
            distanceKm = 1.4,
            supplies = listOf("Medical Bay", "Baby Formula", "Dry Blankets", "Satellite Comms")
        ),
        ShelterFacility(
            name = "Kadri Hills Disaster Relief Pavilion",
            elevationMeters = 65,
            capacity = 600,
            currentOccupancy = 130,
            address = "Near Kadri Temple Grounds, Mangaluru",
            distanceKm = 2.7,
            supplies = listOf("Drinking Water", "Sanitary Kits", "Ambulance Standby")
        )
    )
}
