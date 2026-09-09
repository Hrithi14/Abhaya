package com.pbrlm.abhaya.domain.model

import java.util.Date

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Date = Date(),
    val isAvailable: Boolean = true
) {
    val latFormatted: String get() = String.format("%.6f", latitude)
    val lngFormatted: String get() = String.format("%.6f", longitude)
    val accuracyFormatted: String get() = "${accuracy.toInt()} m"

    companion object {
        val UNAVAILABLE = LocationData(0.0, 0.0, 0f, isAvailable = false)
    }
}
