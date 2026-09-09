package com.pbrlm.abhaya.domain.integration

/**
 * Integration interface for MODULE 1 — Hazard Ingestion (teammate module).
 *
 * Emergency requests can be linked to hazard reports once this module is integrated.
 * The hazardId, clusterId, and zoneId fields on EmergencyRequest are the join points.
 */
interface HazardDataProvider {
    /**
     * Returns the nearest hazard cluster ID for given coordinates.
     * Returns null if no hazard cluster is nearby.
     */
    suspend fun getNearestClusterId(latitude: Double, longitude: Double, radiusMeters: Double = 500.0): String?

    /**
     * Returns the hazard ID for a given cluster.
     */
    suspend fun getHazardIdForCluster(clusterId: String): String?
}
