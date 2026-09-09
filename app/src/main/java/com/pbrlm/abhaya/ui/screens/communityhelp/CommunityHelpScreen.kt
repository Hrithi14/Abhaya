package com.pbrlm.abhaya.ui.screens.communityhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.model.EmergencyPriority
import com.pbrlm.abhaya.navigation.NavRoutes
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHelpScreen(
    navController: NavController,
    viewModel: CommunityHelpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🤝 Community Help", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        bottomBar = { AbhayaBottomNavBar(navController) },
        containerColor = BackgroundDark
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmergencyRed)
            }
        } else if (uiState.emergencies.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤝", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No Active Emergencies", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("No emergencies need community assistance right now.",
                        color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${uiState.emergencies.size} emergency/emergencies need help",
                        color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(uiState.emergencies, key = { it.emergencyRequestId }) { request ->
                    CommunityHelpCard(
                        request = request,
                        onViewRequest = {
                            navController.navigate(NavRoutes.EmergencyDetail.createRoute(request.emergencyRequestId))
                        },
                        onOfferHelp = {
                            navController.navigate(NavRoutes.OfferHelp.createRoute(request.emergencyRequestId))
                        }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun CommunityHelpCard(
    request: EmergencyRequest,
    onViewRequest: () -> Unit,
    onOfferHelp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (request.priority == EmergencyPriority.CRITICAL)
                Color(0xFF2D1212) else SurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(request.emergencyType.emoji, fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "HELP NEEDED",
                            color = if (request.priority == EmergencyPriority.CRITICAL) EmergencyRed else MediumAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = request.emergencyType.displayName,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
                PriorityBadge(request.priority)
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip("👥 ${request.numberOfPeople} people")
                if (request.peopleTrapped) InfoChip("⚠️ Trapped")
                if (request.medicalRequired) InfoChip("🏥 Medical")
            }

            if (request.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = request.description.take(100) + if (request.description.length > 100) "…" else "",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onViewRequest,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Text("VIEW", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onOfferHelp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ActionGreen)
                ) {
                    Text("OFFER HELP", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}
