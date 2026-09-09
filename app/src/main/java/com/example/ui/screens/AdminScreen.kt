package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HazardRepository
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier
) {
    val reports by HazardRepository.reports.collectAsState()

    var offlineCachingEnabled by remember { mutableStateOf(true) }
    var autoSirenEnabled by remember { mutableStateOf(true) }
    var highAccuracyGps by remember { mutableStateOf(true) }

    val totalSubmitted = reports.size
    val totalVerified = reports.count { it.verified }
    val totalUpvotes = reports.sumOf { it.upvotes }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
    ) {
        // Admin Top Bar
        Surface(
            color = DarkNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BlueAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = DarkNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ADMIN & OPERATIONAL CONSOLE",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = SurfaceWhite
                    )
                    Text(
                        text = "ABHAYA Disaster Coordination • Mangaluru Node",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User & Officer Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(DarkSlate, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Volunteer Field Responder",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = DarkNavy
                            )
                            Text(
                                text = "Badge ID: KA-19-MNG-402 • Auth: Verified",
                                fontSize = 12.sp,
                                color = MutedSlate
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(GreenVerifiedBg, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenVerified)
                        }
                    }
                }
            }

            // Real-Time Stats Overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stat 1
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalSubmitted.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryOrange
                            )
                            Text(
                                text = "Reports Active",
                                fontSize = 11.sp,
                                color = MutedSlate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Stat 2
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalVerified.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = GreenVerified
                            )
                            Text(
                                text = "Verified Reports",
                                fontSize = 11.sp,
                                color = MutedSlate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Stat 3
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalUpvotes.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0284C7)
                            )
                            Text(
                                text = "Total Upvotes",
                                fontSize = 11.sp,
                                color = MutedSlate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Basic Settings Section
            item {
                Text(
                    text = "BASIC SYSTEM SETTINGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarkSlate,
                    letterSpacing = 0.5.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Setting 1: Offline tile caching
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Offline OpenStreetMap Caching", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavy)
                                Text("Pre-download tiles for low-connectivity zones", fontSize = 11.sp, color = MutedSlate)
                            }
                            Switch(
                                checked = offlineCachingEnabled,
                                onCheckedChange = { offlineCachingEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrange.copy(alpha = 0.4f))
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSlate)

                        // Setting 2: Flash Flood Auto-Siren
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Severe Flash Flood Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavy)
                                Text("Audible alert when within 500m of rising flood", fontSize = 11.sp, color = MutedSlate)
                            }
                            Switch(
                                checked = autoSirenEnabled,
                                onCheckedChange = { autoSirenEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrange.copy(alpha = 0.4f))
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSlate)

                        // Setting 3: Continuous GPS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Continuous Real-Time GPS Tracking", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavy)
                                Text("Update position continuously during movement", fontSize = 11.sp, color = MutedSlate)
                            }
                            Switch(
                                checked = highAccuracyGps,
                                onCheckedChange = { highAccuracyGps = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrange.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }

            // Moderation & Verification Actions
            item {
                Text(
                    text = "MANAGE & VERIFY REPORTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarkSlate,
                    letterSpacing = 0.5.sp
                )
            }

            items(reports) { report ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${report.category.displayName} (${report.id})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkNavy
                            )

                            Button(
                                onClick = {
                                    HazardRepository.toggleVerification(report.id)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (report.verified) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (report.verified) "Mark Unverified" else "Verify Report",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.verified) GreenVerified else Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.description,
                            fontSize = 12.sp,
                            color = DarkSlate
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upvotes: ${report.upvotes} • Lat: ${String.format("%.3f", report.latitude)}, Lon: ${String.format("%.3f", report.longitude)}",
                                fontSize = 11.sp,
                                color = MutedSlate
                            )

                            Text(
                                text = "Delete",
                                fontSize = 11.sp,
                                color = EmergencyRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { HazardRepository.deleteReport(report.id) }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
