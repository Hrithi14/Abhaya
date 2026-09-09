package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.HazardCategory
import com.example.ui.theme.*

@Composable
fun ReportScreen(
    currentLat: Double,
    currentLon: Double,
    onReportSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(HazardCategory.FLOOD_WATER) }
    var description by remember { mutableStateOf("") }
    var waterDepth by remember { mutableStateOf("") }
    var hasPhotoAttached by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotification by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(16.dp)
            .padding(bottom = 70.dp)
    ) {
        // Top Heading
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PrimaryOrange, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAlert,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "REPORT A HAZARD",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = DarkNavy
                )
                Text(
                    text = "Alert responders and citizens instantly",
                    fontSize = 12.sp,
                    color = MutedSlate
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Success message banner
        if (successNotification) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenVerifiedBg)
                    .border(1.dp, GreenVerified, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenVerified)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Report published live to OpenStreetMap feed!",
                        color = Color(0xFF14532D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Automatic GPS Location Lock Card (Read-only guarantee)
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE0F2FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AUTOMATIC GPS LOCK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0369A1)
                    )
                    Text(
                        text = "Lat: ${String.format("%.5f", currentLat)}, Lon: ${String.format("%.5f", currentLon)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkNavy
                    )
                    Text(
                        text = "Mangaluru Coastal Zone • Accuracy ±4m",
                        fontSize = 11.sp,
                        color = MutedSlate
                    )
                }
                Box(
                    modifier = Modifier
                        .background(GreenVerifiedBg, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("LOCKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenVerified)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hazard Category Selector Title
        Text(
            text = "1. SELECT HAZARD CATEGORY",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = DarkSlate,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Grid of Category Chips
        val categoryRows = HazardCategory.entries.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (rowCategories in categoryRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (category in rowCategories) {
                        val isSelected = category == selectedCategory
                        val chipBg = if (isSelected) PrimaryOrange else SurfaceWhite
                        val chipText = if (isSelected) SurfaceWhite else DarkNavy
                        val chipBorder = if (isSelected) PrimaryOrange else BorderSlate

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedCategory = category },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = SurfaceWhite,
                                        unselectedColor = MutedSlate
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = chipText
                                )
                            }
                        }
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Description Input
        Text(
            text = "2. DESCRIPTION",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = DarkSlate,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                errorMessage = null
            },
            placeholder = {
                Text(
                    "e.g. Waist-deep flooding on service road, power transformer sparking...",
                    fontSize = 13.sp,
                    color = MutedSlate
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("hazard_description_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = BorderSlate
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Water Depth (Optional)
        if (selectedCategory == HazardCategory.FLOOD_WATER || selectedCategory == HazardCategory.WATERLOGGING) {
            Text(
                text = "3. WATER DEPTH (OPTIONAL)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = DarkSlate,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = waterDepth,
                onValueChange = { waterDepth = it },
                placeholder = { Text("e.g. 2.5 ft / 75 cm / Knee-level", fontSize = 13.sp, color = MutedSlate) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hazard_depth_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = BorderSlate
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Photo Attachment (Optional)
        Text(
            text = "4. PHOTO ATTACHMENT (OPTIONAL)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = DarkSlate,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (hasPhotoAttached) Color(0xFFF0FDF4) else SurfaceWhite)
                .border(
                    1.dp,
                    if (hasPhotoAttached) GreenVerified else BorderSlate,
                    RoundedCornerShape(8.dp)
                )
                .clickable { hasPhotoAttached = !hasPhotoAttached }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasPhotoAttached) Icons.Default.CheckCircle else Icons.Default.PhotoCamera,
                        contentDescription = "Attach Photo",
                        tint = if (hasPhotoAttached) GreenVerified else PrimaryOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (hasPhotoAttached) "Photo attached (mangalore_hazard_snap.jpg)" else "Tap to attach hazard scene photo",
                        fontSize = 13.sp,
                        fontWeight = if (hasPhotoAttached) FontWeight.Bold else FontWeight.Normal,
                        color = if (hasPhotoAttached) Color(0xFF14532D) else DarkSlate
                    )
                }

                if (hasPhotoAttached) {
                    Text("REMOVE", fontSize = 11.sp, color = EmergencyRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage ?: "",
                color = EmergencyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (description.isBlank()) {
                    errorMessage = "Please enter a brief description of the hazard."
                    return@Button
                }
                isSubmitting = true
                HazardRepository.addReport(
                    category = selectedCategory,
                    description = description.trim(),
                    latitude = currentLat,
                    longitude = currentLon,
                    waterDepth = waterDepth.trim(),
                    imageUrl = if (hasPhotoAttached) "photo_ref_mangaluru" else null
                )
                isSubmitting = false
                successNotification = true
                description = ""
                waterDepth = ""
                hasPhotoAttached = false
                onReportSubmitted()
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_hazard_report_button")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SurfaceWhite)
            } else {
                Icon(Icons.Default.Send, contentDescription = null, tint = SurfaceWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SUBMIT TO LIVE MAP",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = SurfaceWhite
                )
            }
        }
    }
}
