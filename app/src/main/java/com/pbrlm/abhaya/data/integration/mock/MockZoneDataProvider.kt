package com.pbrlm.abhaya.data.integration.mock

import com.pbrlm.abhaya.domain.integration.ZoneDataProvider
import com.pbrlm.abhaya.domain.integration.ZoneInfo
import com.pbrlm.abhaya.domain.integration.ZoneStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MOCK implementation — returns a simulated RED zone for demo/presentation.
 * Replace with real implementation when MODULE 3 is integrated.
 * See INTEGRATION.md for instructions.
 */
@Singleton
class MockZoneDataProvider @Inject constructor() : ZoneDataProvider {

    override suspend fun getZoneForLocation(latitude: Double, longitude: Double): ZoneInfo {
        return ZoneInfo(
            zoneId             = "ZONE-DEMO-001",
            zoneStatus         = ZoneStatus.RED,
            riskLevel          = 85,
            centerLatitude     = latitude,
            centerLongitude    = longitude,
            radiusMeters       = 500.0,
            activeIncidentCount= 3,
            floodSeverity      = "HIGH",
            waterRising        = true,
            lastUpdated        = System.currentTimeMillis()
        )
    }

    override fun observeZoneForLocation(latitude: Double, longitude: Double): Flow<ZoneInfo?> =
        flow { emit(getZoneForLocation(latitude, longitude)) }
}
