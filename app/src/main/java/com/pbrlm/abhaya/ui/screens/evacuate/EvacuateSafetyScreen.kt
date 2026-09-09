package com.pbrlm.abhaya.ui.screens.evacuate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pbrlm.abhaya.domain.integration.EvacuationRoute
import com.pbrlm.abhaya.domain.integration.RouteStatus
import com.pbrlm.abhaya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvacuateSafetyScreen(
    navController: NavController,
    viewModel: EvacuateSafetyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏃", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Evacuate to Safety", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadRoute) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = ActionGreen)
                        Spacer(Modifier.height(12.dp))
                        Text("Calculating safe route…", color = TextSecondary, fontSize = 14.sp)
                    }
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Route Unavailable", color = EmergencyRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = viewModel::loadRoute,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                uiState.route != null -> {
                    RouteContent(route = uiState.route!!)
                }
            }
        }
    }
}

@Composable
private fun RouteContent(route: EvacuationRoute) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status banner
        val bannerColor = when (route.routeStatus) {
            RouteStatus.AVAILABLE    -> ActionGreen
            RouteStatus.ALL_CLEAR    -> ActionTeal
            RouteStatus.CALCULATING  -> MediumAmber
            RouteStatus.UNAVAILABLE  -> EmergencyRed
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(bannerColor.copy(alpha = 0.15f))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (route.routeStatus) {
                        RouteStatus.AVAILABLE   -> "✅"
                        RouteStatus.ALL_CLEAR   -> "🟢"
                        RouteStatus.CALCULATING -> "⏳"
                        RouteStatus.UNAVAILABLE -> "⛔"
                    },
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = route.routeStatus.displayName.uppercase(),
                        color = bannerColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Safe evacuation route identified",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Destination card
        RouteInfoCard(title = "SAFE DESTINATION") {
            Text(route.safeDestination, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                RouteMetric("Distance", "${route.distanceKm} km")
                RouteMetric("ETA", "${route.estimatedTimeMinutes} min")
            }
        }

        // Route instructions
        RouteInfoCard(title = "ROUTE") {
            Text(route.safeRoute, color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp)
        }

        // High elevation point
        RouteInfoCard(title = "HIGH-ELEVATION SAFE LOCATION") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⛰️", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(route.nearestHighElevation, color = TextPrimary, fontSize = 14.sp)
            }
        }

        // Relief centre
        RouteInfoCard(title = "NEAREST RELIEF CENTER") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏥", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(route.nearestReliefCenter, color = TextPrimary, fontSize = 14.sp)
            }
        }

        // Avoided red zones
        if (route.avoidedRedZones.isNotEmpty()) {
            RouteInfoCard(title = "AVOIDED RED ZONES") {
                route.avoidedRedZones.forEachIndexed { index, zone ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚫", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(zone, color = EmergencyRed, fontSize = 13.sp)
                    }
                    if (index < route.avoidedRedZones.lastIndex) Spacer(Modifier.height(4.dp))
                }
            }
        }

        // Integration note
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("ℹ️", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Route data is provided by the PBRLM elevation-aware routing module. Live map integration coming soon.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RouteInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = ActionGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun RouteMetric(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
