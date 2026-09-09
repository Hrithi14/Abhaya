package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HazardRepository
import com.example.ui.components.SOSButton
import com.example.ui.theme.*

@Composable
fun EmergencyScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contacts = HazardRepository.emergencyContacts
    val shelters = HazardRepository.safeShelters

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
    ) {
        // Top Bar
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
                        .size(32.dp)
                        .background(EmergencyRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Emergency,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "EMERGENCY & DISASTER RELIEF",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = SurfaceWhite
                    )
                    Text(
                        text = "24/7 Rapid Help • Karnataka Disaster Management",
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
            // Main Emergency 112 Banner
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NATIONAL EMERGENCY NUMBER",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = EmergencyRed,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unified Police, Ambulance, Fire & Flood Rescue",
                            fontSize = 12.sp,
                            color = MutedSlate
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Large red Call 112 Action
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:112")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("emergency_screen_call_112_button")
                        ) {
                            Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = SurfaceWhite)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CALL 112 NOW",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = SurfaceWhite,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Safe Shelters Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SAFE HIGH-GROUND SHELTERS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = DarkNavy
                    )
                }
            }

            items(shelters) { shelter ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = shelter.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarkNavy
                                )
                                Text(
                                    text = shelter.address,
                                    fontSize = 12.sp,
                                    color = MutedSlate
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${shelter.elevationMeters}m Elevation",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Capacity & Distance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Occupancy: ${shelter.currentOccupancy} / ${shelter.capacity} citizens",
                                fontSize = 12.sp,
                                color = DarkSlate,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${shelter.distanceKm} km away",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrangeDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Supplies available
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            shelter.supplies.forEach { supply ->
                                Box(
                                    modifier = Modifier
                                        .background(BackgroundLight, RoundedCornerShape(4.dp))
                                        .border(1.dp, BorderSlate, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(supply, fontSize = 10.sp, color = DarkSlate)
                                }
                            }
                        }
                    }
                }
            }

            // Nearest Hospitals & Police
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                ) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEAREST HOSPITALS & POLICE UNITS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = DarkNavy
                    )
                }
            }

            items(contacts) { contact ->
                Card(
                    shape = RoundedCornerShape(10.dp),
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
                                .size(36.dp)
                                .background(
                                    if (contact.type == "HOSPITAL") Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (contact.type == "HOSPITAL") Icons.Default.LocalHospital else Icons.Default.LocalPolice,
                                contentDescription = null,
                                tint = if (contact.type == "HOSPITAL") EmergencyRed else GreenVerified,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkNavy
                            )
                            Text(
                                text = contact.address,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                            if (contact.note != null) {
                                Text(
                                    text = contact.note,
                                    fontSize = 11.sp,
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone.replace(" ", "")}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(PrimaryOrange.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call ${contact.name}",
                                tint = PrimaryOrangeDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
