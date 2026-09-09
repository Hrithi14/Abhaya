package com.pbrlm.abhaya.domain.integration

import kotlinx.coroutines.flow.Flow

/**
 * Integration interface for MODULE 3 — Zone Classification (teammate module).
 *
 * Implement this in the zone module and bind it in AppModule.kt.
 * The mock implementation (MockZoneDataProvider) is used until then.
 */
interface ZoneDataProvider {
    suspend fun getZoneForLocation(latitude: Double, longitude: Double): ZoneInfo?
    fun observeZoneForLocation(latitude: Double, longitude: Double): Flow<ZoneInfo?>
}

data class ZoneInfo(
    val zoneId: String,
    val zoneStatus: ZoneStatus,
    val riskLevel: Int,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Double,
    val activeIncidentCount: Int,
    val floodSeverity: String,
    val waterRising: Boolean,
    val lastUpdated: Long
)

enum class ZoneStatus(val displayName: String) {
    GREEN("Safe Zone"),
    YELLOW("Caution Zone"),
    RED("Danger Zone")
}
