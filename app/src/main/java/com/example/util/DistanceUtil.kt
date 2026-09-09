package com.example.util

import kotlin.math.*

object DistanceUtil {
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculate distance between two GPS coordinates using the Haversine formula
     */
    fun calculateDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val originLat = Math.toRadians(lat1)
        val destinationLat = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(originLat) * cos(destinationLat)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c * 1000.0
    }

    fun formatDistance(meters: Double): String {
        return if (meters < 1000) {
            "${meters.roundToInt()} m"
        } else {
            String.format("%.1f km", meters / 1000.0)
        }
    }

    fun formatTimeAgo(timestamp: Long): String {
        val diffMs = System.currentTimeMillis() - timestamp
        val minutes = diffMs / (1000 * 60)
        return when {
            minutes <= 1 -> "Just now"
            minutes < 60 -> "$minutes mins ago"
            minutes < 1440 -> "${minutes / 60} hours ago"
            else -> "${minutes / 1440} days ago"
        }
    }
}
