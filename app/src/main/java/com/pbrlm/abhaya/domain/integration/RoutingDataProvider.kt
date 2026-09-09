package com.pbrlm.abhaya.domain.integration

import kotlinx.coroutines.flow.Flow

/**
 * Integration interface for MODULE 6 — Evacuation Routing (teammate module).
 *
 * Implement this in the routing module and bind it in AppModule.kt.
 * The mock implementation (MockRoutingDataProvider) is used until then.
 */
interface RoutingDataProvider {
    suspend fun getEvacuationRoute(
        currentLatitude: Double,
        currentLongitude: Double
    ): EvacuationRoute?

    fun observeEvacuationRoute(
        currentLatitude: Double,
        currentLongitude: Double
    ): Flow<EvacuationRoute?>
}

data class EvacuationRoute(
    val currentLatitude: Double,
    val currentLongitude: Double,
    val safeDestination: String,
    val safeRoute: String,
    val distanceKm: Double,
    val estimatedTimeMinutes: Int,
    val nearestHighElevation: String,
    val nearestReliefCenter: String,
    val avoidedRedZones: List<String>,
    val routeStatus: RouteStatus
)

enum class RouteStatus(val displayName: String) {
    AVAILABLE("Route Available"),
    CALCULATING("Calculating Route"),
    UNAVAILABLE("Route Unavailable"),
    ALL_CLEAR("All Clear — No Evacuation Needed")
}
