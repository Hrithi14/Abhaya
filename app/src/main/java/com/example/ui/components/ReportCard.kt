package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HazardCategory
import com.example.model.HazardReport
import com.example.ui.theme.*
import com.example.util.DistanceUtil

@Composable
fun ReportCard(
    report: HazardReport,
    userLat: Double,
    userLon: Double,
    onCardClick: () -> Unit,
    onUpvoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val distanceMeters = DistanceUtil.calculateDistanceMeters(
        userLat, userLon, report.latitude, report.longitude
    )
    val distanceFormatted = DistanceUtil.formatDistance(distanceMeters)
    val timeAgo = DistanceUtil.formatTimeAgo(report.createdAt)

    val badgeColor = when (report.category) {
        HazardCategory.FLOOD_WATER, HazardCategory.WATERLOGGING -> PrimaryOrange
        HazardCategory.FIRE, HazardCategory.MEDICAL_EMERGENCY -> EmergencyRed
        HazardCategory.OPEN_WIRE, HazardCategory.ROADBLOCK -> AmberAlert
        else -> Color(0xFFEA580C)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("report_card_${report.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category | Time & Verification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(badgeColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (report.category) {
                            HazardCategory.FLOOD_WATER -> Icons.Default.Water
                            HazardCategory.WATERLOGGING -> Icons.Default.WaterDamage
                            HazardCategory.FALLEN_TREE -> Icons.Default.Park
                            HazardCategory.ROADBLOCK -> Icons.Default.Traffic
                            HazardCategory.POTHOLE -> Icons.Default.Warning
                            HazardCategory.OPEN_WIRE -> Icons.Default.Bolt
                            HazardCategory.LANDSLIDE -> Icons.Default.Landscape
                            HazardCategory.FIRE -> Icons.Default.LocalFireDepartment
                            HazardCategory.MEDICAL_EMERGENCY -> Icons.Default.LocalHospital
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${report.category.displayName} | $timeAgo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }

                // Verification Badge
                if (report.verified) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GreenVerifiedBg)
                            .border(1.dp, GreenVerified.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VERIFIED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GreenVerified
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AmberAlertBg)
                            .border(1.dp, AmberAlert.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NOT VERIFIED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = report.description,
                fontSize = 13.sp,
                color = DarkSlate,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Optional Water Depth Tag
            if (!report.waterDepth.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Water Depth: ${report.waterDepth}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: GPS, Distance, and Upvotes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MutedSlate,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "GPS: ${String.format("%.3f", report.latitude)}, ${String.format("%.3f", report.longitude)}",
                        fontSize = 11.sp,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = distanceFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryOrangeDark
                    )
                }

                // Upvotes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onUpvoteClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("upvote_button_${report.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Upvote",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${report.upvotes} votes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }
            }
        }
    }
}
