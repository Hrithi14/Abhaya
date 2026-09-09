package com.pbrlm.abhaya.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyDetailScreen(
    emergencyId: String,
    navController: NavController,
    viewModel: EmergencyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(emergencyId) { viewModel.load(emergencyId) }

    if (uiState.showCancelDialog) {
        ConfirmDialog(
            title       = "Cancel Emergency?",
            message     = "This will mark the request as cancelled. Are you sure?",
            confirmText = "Yes, Cancel",
            onConfirm   = viewModel::cancelRequest,
            onDismiss   = viewModel::dismissCancelDialog
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Details", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EmergencyRed)
                }
            }
            uiState.request == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Emergency not found.", color = TextSecondary)
                }
            }
            else -> {
                DetailContent(
                    request = uiState.request!!,
                    modifier = Modifier.padding(padding),
                    onCallResponder = viewModel::callResponder,
                    onCancel = viewModel::showCancelDialog,
                    cancelSuccess = uiState.cancelSuccess
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    request: EmergencyRequest,
    modifier: Modifier,
    onCallResponder: () -> Unit,
    onCancel: () -> Unit,
    cancelSuccess: Boolean
) {
    val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header badge row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(request.emergencyType.emoji, fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text(request.emergencyType.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            PriorityBadge(request.priority)
        }

        // Status
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(request.status)
            if (cancelSuccess) {
                Spacer(Modifier.width(8.dp))
                Text("Cancelled successfully", color = StatusResolved, fontSize = 12.sp)
            }
        }

        HorizontalDivider(color = DividerColor)

        // Core info card
        InfoCard {
            DetailRow("Emergency ID",  request.emergencyRequestId)
            DetailRow("Timestamp",     fmt.format(request.timestamp))
            DetailRow("Latitude",      String.format("%.6f", request.latitude))
            DetailRow("Longitude",     String.format("%.6f", request.longitude))
            DetailRow("GPS Accuracy",  "${request.gpsAccuracy.toInt()} m")
            DetailRow("People",        request.numberOfPeople.toString())
            if (request.buildingFloor != null) DetailRow("Building/Floor", request.buildingFloor)
            if (request.contactNumber.isNotBlank()) DetailRow("Contact", request.contactNumber)
        }

        // Water details
        if (request.waterDepth != null) {
            InfoCard(title = "Water Conditions") {
                DetailRow("Water Depth", request.waterDepth.displayName)
                if (request.waterRising != null) DetailRow("Water Rising", if (request.waterRising) "Yes" else "No")
                DetailRow("People Trapped", if (request.peopleTrapped) "Yes" else "No")
            }
        }

        // Medical details
        if (request.medicalEmergencyType != null) {
            InfoCard(title = "Medical Details") {
                DetailRow("Medical Type", request.medicalEmergencyType.displayName)
                if (request.personUnconscious) DetailRow("Unconscious", "Yes")
                if (request.breathingProblem)  DetailRow("Breathing Problem", "Yes")
                if (request.severeBleeding)    DetailRow("Severe Bleeding", "Yes")
                if (request.pregnancyRelated)  DetailRow("Pregnancy Related", "Yes")
            }
        }

        // Vulnerable people
        val vulnerableList = buildList {
            if (request.children) add("Children")
            if (request.elderly)  add("Elderly")
            if (request.disabled) add("Disabled")
            if (request.injured)  add("Injured")
        }
        if (vulnerableList.isNotEmpty()) {
            InfoCard(title = "Vulnerable / Injured") {
                DetailRow("Affected", vulnerableList.joinToString(", "))
            }
        }

        // Description
        if (request.description.isNotBlank()) {
            InfoCard(title = "Description") {
                Text(request.description, color = TextPrimary, fontSize = 14.sp)
            }
        }

        // Photo
        if (request.photoUrl != null) {
            Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(Modifier.padding(12.dp)) {
                    Text("PHOTO", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    AsyncImage(
                        model = request.photoUrl,
                        contentDescription = "Emergency photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }
        }

        // Responder info
        if (request.assignedResponderId != null) {
            InfoCard(title = "Responder") {
                DetailRow("Responder ID", request.assignedResponderId)
                if (request.responderContact != null) DetailRow("Contact", request.responderContact)
                if (request.responseTime != null) DetailRow("Response Time", fmt.format(request.responseTime))
                if (request.resolutionTime != null) DetailRow("Resolved At", fmt.format(request.resolutionTime))
                if (!request.responderNotes.isNullOrBlank()) DetailRow("Notes", request.responderNotes)
            }
        }

        if (request.escalationRequired) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🚨 Escalation required — this emergency has been flagged for priority response.",
                    color = EmergencyRed, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // Action buttons
        if (request.assignedResponderId != null && request.responderContact != null) {
            Button(
                onClick = onCallResponder,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ActionBlue)
            ) {
                Icon(Icons.Default.Call, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("CALL RESPONDER", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!request.status.isTerminal) {
            OutlinedButton(
                onClick  = onCancel,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                border   = BorderStroke(1.dp, DividerColor)
            ) {
                Text("CANCEL REQUEST", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun InfoCard(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (title != null) {
                Text(title.uppercase(), color = EmergencyRed, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(8.dp))
            }
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.weight(0.45f))
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.55f))
    }
}
