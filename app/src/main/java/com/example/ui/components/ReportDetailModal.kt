package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HazardCategory
import com.example.model.HazardReport
import com.example.ui.theme.*
import com.example.util.DistanceUtil

@Composable
fun ReportDetailModal(
    report: HazardReport?,
    userLat: Double,
    userLon: Double,
    onDismiss: () -> Unit,
    onUpvote: () -> Unit,
    onFocusOnMap: () -> Unit
) {
    if (report == null) return

    val distanceMeters = DistanceUtil.calculateDistanceMeters(
        userLat, userLon, report.latitude, report.longitude
    )
    val distanceFormatted = DistanceUtil.formatDistance(distanceMeters)
    val timeAgo = DistanceUtil.formatTimeAgo(report.createdAt)

    val badgeColor = when (report.category) {
        HazardCategory.FLOOD_WATER, HazardCategory.WATERLOGGING -> PrimaryOrange
        HazardCategory.FIRE, HazardCategory.MEDICAL_EMERGENCY -> EmergencyRed
        HazardCategory.OPEN_WIRE, HazardCategory.ROADBLOCK -> AmberAlert
        else -> Color(0xFFD97706)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("report_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Row: Category Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(badgeColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = report.category.displayName,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = DarkNavy
                            )
                            Text(
                                text = timeAgo,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MutedSlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification status pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (report.verified) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GreenVerifiedBg)
                                .border(1.dp, GreenVerified, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "VERIFIED HAZARD REPORT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenVerified
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberAlertBg)
                                .border(1.dp, AmberAlert, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "COMMUNITY REPORT (UNVERIFIED)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = report.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = DarkSlate
                )

                // Optional Water Depth
                if (!report.waterDepth.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F9FF))
                            .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estimated Water Depth: ${report.waterDepth}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0369A1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Location details Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GPS: ${report.latitude}, ${report.longitude}",
                                fontSize = 12.sp,
                                color = DarkSlate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NearMe, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Distance from user: $distanceFormatted",
                                fontSize = 12.sp,
                                color = DarkSlate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: Upvote & Locate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onUpvote,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upvote (${report.upvotes})", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onFocusOnMap()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View on Map", fontSize = 12.sp, color = SurfaceWhite)
                    }
                }
            }
        }
    }
}
