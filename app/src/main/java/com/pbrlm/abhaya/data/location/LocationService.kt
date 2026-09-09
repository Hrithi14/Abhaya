package com.pbrlm.abhaya.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.pbrlm.abhaya.domain.model.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    /**
     * Returns the best last known location.
     * Call this when creating an emergency to get current GPS coordinates.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            isAvailable = true
                        )
                    )
                } else {
                    // Fallback to last known location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            continuation.resume(
                                LocationData(
                                    latitude = lastLoc.latitude,
                                    longitude = lastLoc.longitude,
                                    accuracy = lastLoc.accuracy,
                                    isAvailable = true
                                )
                            )
                        } else {
                            continuation.resume(LocationData.UNAVAILABLE)
                        }
                    }.addOnFailureListener {
                        continuation.resume(LocationData.UNAVAILABLE)
                    }
                }
            }.addOnFailureListener {
                continuation.resume(LocationData.UNAVAILABLE)
            }
        }
    }

    /**
     * Continuous location updates flow.
     * Used for the live GPS display on the Emergency Home screen.
     */
    @SuppressLint("MissingPermission")
    fun observeLocation(): Flow<LocationData> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L // 5 second updates
        ).setMinUpdateIntervalMillis(3_000L).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        LocationData(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracy = loc.accuracy,
                            isAvailable = true
                        )
                    )
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, null)

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    /**
     * Checks if location services are enabled on the device.
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
