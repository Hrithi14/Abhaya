package com.pbrlm.abhaya.ui.screens.communityhelp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pbrlm.abhaya.domain.model.HelpType
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferHelpScreen(
    emergencyId: String,
    navController: NavController,
    viewModel: CommunityHelpViewModel = hiltViewModel()
) {
    var selectedHelpType by remember { mutableStateOf(HelpType.OTHER) }
    var message by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Text("✅", fontSize = 56.sp)
                Spacer(Modifier.height(16.dp))
                Text("Help Offered!", color = ActionGreen, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("Your offer has been recorded. The citizen may contact you.",
                    color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("DONE", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offer Help", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Emergency: $emergencyId", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))

            FormSectionHeader("What Can You Offer?")

            // Help type grid
            val rows = HelpType.values().toList().chunked(3)
            rows.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { type ->
                        ToggleChip(
                            label = "${type.emoji} ${type.displayName}",
                            selected = selectedHelpType == type,
                            onToggle = { if (it) selectedHelpType = type },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))
            FormSectionHeader("Message (Optional)")
            AbhayaTextField(
                value = message,
                onValueChange = { message = it },
                label = "Your message",
                placeholder = "Briefly describe how you can help…",
                singleLine = false,
                maxLines = 4
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.offerHelp(emergencyId, selectedHelpType, message)
                    submitted = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ActionGreen)
            ) {
                Text("SUBMIT HELP OFFER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
