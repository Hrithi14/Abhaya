package com.pbrlm.abhaya.data.integration.mock

import com.pbrlm.abhaya.domain.integration.EvacuationRoute
import com.pbrlm.abhaya.domain.integration.RouteStatus
import com.pbrlm.abhaya.domain.integration.RoutingDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MOCK implementation — returns a demo evacuation route for presentation.
 * Replace with real implementation when MODULE 6 is integrated.
 * See INTEGRATION.md for instructions.
 */
@Singleton
class MockRoutingDataProvider @Inject constructor() : RoutingDataProvider {

    override suspend fun getEvacuationRoute(
        currentLatitude: Double,
        currentLongitude: Double
    ): EvacuationRoute {
        return EvacuationRoute(
            currentLatitude      = currentLatitude,
            currentLongitude     = currentLongitude,
            safeDestination      = "Government Higher Secondary School, High Ground",
            safeRoute            = "Take MG Road north → Turn right at Post Office → Continue 1.2 km to school",
            distanceKm           = 1.8,
            estimatedTimeMinutes = 22,
            nearestHighElevation = "Hill View Park (Elevation: 48 m)",
            nearestReliefCenter  = "Govt. HS School — Relief Center (0.8 km)",
            avoidedRedZones      = listOf("River Junction RED Zone", "Market Area RED Zone"),
            routeStatus          = RouteStatus.AVAILABLE
        )
    }

    override fun observeEvacuationRoute(
        currentLatitude: Double,
        currentLongitude: Double
    ): Flow<EvacuationRoute?> =
        flow { emit(getEvacuationRoute(currentLatitude, currentLongitude)) }
}
