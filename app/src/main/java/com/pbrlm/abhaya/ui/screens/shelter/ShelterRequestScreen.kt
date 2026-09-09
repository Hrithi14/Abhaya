package com.pbrlm.abhaya.ui.screens.shelter

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
import com.pbrlm.abhaya.navigation.NavRoutes
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.screens.common.SubmissionSuccessScreen
import com.pbrlm.abhaya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterRequestScreen(
    navController: NavController,
    viewModel: ShelterViewModel = hiltViewModel()
) {
    val form by viewModel.form.collectAsState()

    if (form.submittedRequest != null) {
        SubmissionSuccessScreen(
            request = form.submittedRequest!!,
            onViewRequests = {
                viewModel.clearSubmission()
                navController.navigate(NavRoutes.MyRequests.route) { popUpTo(NavRoutes.EmergencyHome.route) }
            },
            onGoHome = {
                viewModel.clearSubmission()
                navController.navigate(NavRoutes.EmergencyHome.route) { popUpTo(NavRoutes.EmergencyHome.route) { inclusive = true } }
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏠", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Request Shelter", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            FormSectionHeader("People Details")
            NumberStepper(value = form.numberOfPeople, onValueChange = viewModel::updatePeople)
            Spacer(Modifier.height(12.dp))

            Text("Vulnerable People Present", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("Children", form.children, viewModel::updateChildren, Modifier.weight(1f))
                ToggleChip("Elderly",  form.elderly,  viewModel::updateElderly,  Modifier.weight(1f))
                ToggleChip("Disabled", form.disabled, viewModel::updateDisabled, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            FormSectionHeader("Contact & Situation")

            AbhayaTextField(value = form.contactNumber, onValueChange = viewModel::updateContact,
                label = "Contact Number", isError = form.contactError.isNotEmpty(), errorText = form.contactError)
            Spacer(Modifier.height(10.dp))
            AbhayaTextField(value = form.currentSituation, onValueChange = viewModel::updateSituation,
                label = "Current Situation", placeholder = "Briefly describe your current situation…",
                singleLine = false, maxLines = 3)
            Spacer(Modifier.height(10.dp))
            AbhayaTextField(value = form.additionalInfo, onValueChange = viewModel::updateInfo,
                label = "Additional Information", singleLine = false, maxLines = 4)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !form.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
            ) {
                if (form.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("SUBMIT SHELTER REQUEST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
