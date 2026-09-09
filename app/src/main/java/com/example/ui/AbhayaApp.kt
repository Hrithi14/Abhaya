package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.HazardRepository
import com.example.ui.components.AppTab
import com.example.ui.components.BottomNavBar
import com.example.ui.components.SOSButton
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.EmergencyScreen
import com.example.ui.screens.LiveMapScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.theme.BackgroundLight
import com.google.android.gms.location.*

@Composable
fun AbhayaApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AppTab.LIVE_MAP) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Real Live GPS State (Default Mangaluru, Karnataka: 12.9141, 74.8560)
    var userLat by remember { mutableStateOf(HazardRepository.DEFAULT_LATITUDE) }
    var userLon by remember { mutableStateOf(HazardRepository.DEFAULT_LONGITUDE) }
    var isGpsLoading by remember { mutableStateOf(true) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Permission launcher for fine location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            startLocationUpdates(fusedLocationClient) { lat, lon ->
                userLat = lat
                userLon = lon
                isGpsLoading = false
            }
        } else {
            isGpsLoading = false
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            startLocationUpdates(fusedLocationClient) { lat, lon ->
                userLat = lat
                userLon = lon
                isGpsLoading = false
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            // Main Screen Content
            when (selectedTab) {
                AppTab.LIVE_MAP -> LiveMapScreen(
                    userLat = userLat,
                    userLon = userLon,
                    isGpsLoading = isGpsLoading,
                    onNavigateToEmergency = { selectedTab = AppTab.EMERGENCY }
                )
                AppTab.REPORT -> ReportScreen(
                    currentLat = userLat,
                    currentLon = userLon,
                    onReportSubmitted = { selectedTab = AppTab.LIVE_MAP }
                )
                AppTab.EMERGENCY -> EmergencyScreen()
                AppTab.ADMIN -> AdminScreen()
            }

            // Floating Red SOS / CALL 112 Button (Prominent, Floating above bottom navigation)
            if (selectedTab != AppTab.EMERGENCY) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    SOSButton()
                }
            }
        }
    }
}

private fun startLocationUpdates(
    client: FusedLocationProviderClient,
    onLocationReceived: (Double, Double) -> Unit
) {
    try {
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                onLocationReceived(loc.latitude, loc.longitude)
            }
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    onLocationReceived(location.latitude, location.longitude)
                }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    } catch (_: SecurityException) {
        // Fallback to default coordinates safely
    }
}
