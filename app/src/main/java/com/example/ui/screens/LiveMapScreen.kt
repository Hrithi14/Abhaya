package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HazardRepository
import com.example.model.HazardReport
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LiveMapScreen(
    userLat: Double,
    userLon: Double,
    isGpsLoading: Boolean,
    onNavigateToEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reports by HazardRepository.reports.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedReportForModal by remember { mutableStateOf<HazardReport?>(null) }

    // Map Center and Zoom state
    var mapCenterLat by remember { mutableStateOf(userLat) }
    var mapCenterLon by remember { mutableStateOf(userLon) }
    var zoomLevel by remember { mutableStateOf(14) }

    // Keep map centered if first GPS fix comes in
    LaunchedEffect(userLat, userLon) {
        if (mapCenterLat == HazardRepository.DEFAULT_LATITUDE && mapCenterLon == HazardRepository.DEFAULT_LONGITUDE) {
            mapCenterLat = userLat
            mapCenterLon = userLon
        }
    }

    val dynamicSummary = remember(reports) {
        HazardRepository.getDynamicAISummary()
    }

    val filteredReports = remember(reports, searchQuery) {
        if (searchQuery.isBlank()) {
            reports
        } else {
            reports.filter {
                it.category.displayName.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundLight)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Header
            TopHeader(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onEmergencyBellClick = onNavigateToEmergency,
                alertCount = reports.count { !it.verified }
            )

            // 2. AI Situation Summary Card
            AISituationCard(
                summaryMessage = dynamicSummary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Loading state banner if GPS is locking
            if (isGpsLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AmberAlertBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = PrimaryOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calibrating continuous GPS coordinates...",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. OpenStreetMap Canvas (interactive map)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                OpenStreetMapCompose(
                    modifier = Modifier.fillMaxSize(),
                    centerLat = mapCenterLat,
                    centerLon = mapCenterLon,
                    zoom = zoomLevel,
                    onZoomChange = { zoomLevel = it },
                    onCenterChange = { lat, lon ->
                        mapCenterLat = lat
                        mapCenterLon = lon
                    },
                    userLat = userLat,
                    userLon = userLon,
                    reports = filteredReports,
                    onReportClick = { report ->
                        selectedReportForModal = report
                    },
                    onRecenterClick = {
                        mapCenterLat = userLat
                        mapCenterLon = userLon
                        zoomLevel = 15
                    }
                )
            }

            // 4. Community Reports Feed Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "COMMUNITY REPORTS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = DarkNavy,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(DarkSlate, androidx.compose.foundation.shape.CircleShape)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredReports.size} active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }

                Text(
                    text = "Live Sync • Mangaluru",
                    fontSize = 11.sp,
                    color = MutedSlate
                )
            }

            // 5. Scrollable Community Reports Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                if (filteredReports.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hazard reports match your search.",
                                color = MutedSlate,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(filteredReports, key = { it.id }) { report ->
                        ReportCard(
                            report = report,
                            userLat = userLat,
                            userLon = userLon,
                            onCardClick = {
                                // Animate/center map to report coordinates and zoom
                                mapCenterLat = report.latitude
                                mapCenterLon = report.longitude
                                zoomLevel = 16
                                selectedReportForModal = report
                            },
                            onUpvoteClick = {
                                HazardRepository.upvoteReport(report.id)
                            }
                        )
                    }
                }
            }
        }

        // Details Modal when marker or card tapped
        ReportDetailModal(
            report = selectedReportForModal,
            userLat = userLat,
            userLon = userLon,
            onDismiss = { selectedReportForModal = null },
            onUpvote = {
                selectedReportForModal?.let {
                    HazardRepository.upvoteReport(it.id)
                    selectedReportForModal = it.copy(upvotes = it.upvotes + 1)
                }
            },
            onFocusOnMap = {
                selectedReportForModal?.let {
                    mapCenterLat = it.latitude
                    mapCenterLon = it.longitude
                    zoomLevel = 16
                }
            }
        )
    }
}
