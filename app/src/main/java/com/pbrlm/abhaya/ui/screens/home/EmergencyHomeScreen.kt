package com.pbrlm.abhaya.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.pbrlm.abhaya.domain.integration.ZoneStatus
import com.pbrlm.abhaya.navigation.NavRoutes
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EmergencyHomeScreen(
    navController: NavController,
    viewModel: EmergencyHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationPermission = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!locationPermission.allPermissionsGranted) {
            locationPermission.launchMultiplePermissionRequest()
        }
    }

    // SOS success sheet
    if (uiState.sosResult != null) {
        SosSuccessDialog(
            result = uiState.sosResult!!,
            onDismiss = viewModel::dismissSosResult
        )
    }

    // Call confirmation dialog
    if (uiState.showCallDialog) {
        ConfirmDialog(
            title = uiState.callDialogTitle,
            message = "This will open your phone dialer. You will need to press Call to connect.",
            confirmText = "Open Dialer",
            onConfirm = viewModel::confirmCall,
            onDismiss = viewModel::dismissCallDialog,
            confirmColor = EmergencyRed
        )
    }

    Scaffold(
        bottomBar = { AbhayaBottomNavBar(navController) },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Header ──────────────────────────────────────────────────────
            HomeHeader()

            Spacer(Modifier.height(12.dp))

            // ── Red Zone Alert Banner (if applicable) ────────────────────
            val zone = uiState.zoneInfo
            if (zone != null && zone.zoneStatus == ZoneStatus.RED) {
                RedZoneAlertBanner(
                    onSendSos       = viewModel::sendSos,
                    onCallRescue    = viewModel::requestCallRescue,
                    onEvacuate      = { navController.navigate(NavRoutes.EvacuateSafety.route) },
                    onCallEmergency = viewModel::requestCall112
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── GPS Card ────────────────────────────────────────────────
            GpsStatusCard(
                locationData = uiState.locationData,
                isLoading    = uiState.isLocating
            )

            Spacer(Modifier.height(20.dp))

            // ── SOS Button ───────────────────────────────────────────────
            SosPrimaryButton(onClick = viewModel::sendSos)

            Spacer(Modifier.height(20.dp))

            // ── Emergency Action Grid ────────────────────────────────────
            Text(
                text = "SELECT EMERGENCY TYPE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            EmergencyActionGrid(navController = navController)

            Spacer(Modifier.height(20.dp))

            // ── Call Buttons ─────────────────────────────────────────────
            Text(
                text = "EMERGENCY CONTACTS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CallButton(
                    label = "CALL 112",
                    emoji = "📞",
                    color = EmergencyRed,
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::requestCall112
                )
                CallButton(
                    label = "CALL RESCUE",
                    emoji = "🚨",
                    color = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::requestCallRescue
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick Nav ────────────────────────────────────────────────
            Text(
                text = "QUICK ACCESS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            QuickNavGrid(navController = navController)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "🛡️ ABHAYA",
                color = EmergencyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Emergency Portal",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "PBRLM Monsoon Safety System",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        // Live time
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        Text(
            text = fmt.format(java.util.Date()),
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RedZoneAlertBanner(
    onSendSos: () -> Unit,
    onCallRescue: () -> Unit,
    onEvacuate: () -> Unit,
    onCallEmergency: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF7F0000), Color(0xFFD32F2F))
                )
            )
            .border(1.dp, EmergencyRed, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "RED ZONE ALERT",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "You are near an active high-risk area. Take immediate action.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallAlertButton("SOS", onClick = onSendSos, modifier = Modifier.weight(1f))
                SmallAlertButton("EVACUATE", onClick = onEvacuate, modifier = Modifier.weight(1f), bgColor = Color(0xFF1565C0))
                SmallAlertButton("CALL 112", onClick = onCallEmergency, modifier = Modifier.weight(1f), bgColor = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
private fun SmallAlertButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFF7F0000)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun SosPrimaryButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text("🚨", fontSize = 24.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "SEND SOS",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "Tap to send emergency signal",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun EmergencyActionGrid(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EmergencyActionCard(
                emoji = "🌊",
                title = "Trapped\nby Water",
                color = Color(0xFF0D47A1),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.WaterRescue.route) }
            )
            EmergencyActionCard(
                emoji = "🏥",
                title = "Medical\nEmergency",
                color = Color(0xFF880E4F),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.MedicalEmergency.route) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EmergencyActionCard(
                emoji = "🚤",
                title = "Request\nBoat Rescue",
                color = Color(0xFF1B5E20),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.BoatRescue.route) }
            )
            EmergencyActionCard(
                emoji = "🏠",
                title = "Request\nShelter",
                color = Color(0xFF4A148C),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.ShelterRequest.route) }
            )
        }
    }
}

@Composable
private fun EmergencyActionCard(
    emoji: String,
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.3f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun CallButton(
    label: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickNavGrid(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickNavCard(
                icon = Icons.Default.Warning,
                title = "My Active\nEmergencies",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.MyRequests.route) }
            )
            QuickNavCard(
                icon = Icons.Default.Group,
                title = "Community\nHelp",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.CommunityHelp.route) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickNavCard(
                icon = Icons.Default.History,
                title = "Emergency\nHistory",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(NavRoutes.History.route) }
            )
            QuickNavCard(
                icon = Icons.Default.DirectionsWalk,
                title = "Evacuate\nto Safety",
                modifier = Modifier.weight(1f),
                color = Color(0xFF1B5E20),
                onClick = { navController.navigate(NavRoutes.EvacuateSafety.route) }
            )
        }
    }
}

@Composable
private fun QuickNavCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    color: Color = SurfaceVariant,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (color == SurfaceVariant) EmergencyRed else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                color = if (color == SurfaceVariant) TextPrimary else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SosSuccessDialog(result: SosResult, onDismiss: () -> Unit) {
    val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("✅", fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (result.success) "SOS SENT SUCCESSFULLY" else "SOS FAILED",
                    color = if (result.success) ActionGreen else EmergencyRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            if (result.success) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SosDetailRow("Emergency ID", result.emergencyRequestId)
                    SosDetailRow("Time", fmt.format(result.timestamp))
                    SosDetailRow("Location",
                        if (result.locationData.isAvailable)
                            "${result.locationData.latFormatted}, ${result.locationData.lngFormatted}"
                        else "GPS Unavailable"
                    )
                    SosDetailRow("Priority", "CRITICAL")
                    SosDetailRow("Status", "ACTIVE")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Your emergency has been recorded. If you need to speak to emergency services, use CALL 112.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            } else {
                Text(
                    text = result.errorMessage ?: "Failed to send SOS. Please try again or call 112 directly.",
                    color = EmergencyRed,
                    fontSize = 13.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = if (result.success) ActionGreen else EmergencyRed)
            ) {
                Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun SosDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.6f)
        )
    }
}
