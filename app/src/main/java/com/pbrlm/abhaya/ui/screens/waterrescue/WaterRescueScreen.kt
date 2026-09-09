package com.pbrlm.abhaya.ui.screens.waterrescue

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
import com.pbrlm.abhaya.domain.model.WaterDepth
import com.pbrlm.abhaya.navigation.NavRoutes
import com.pbrlm.abhaya.ui.components.*
import com.pbrlm.abhaya.ui.screens.common.SubmissionSuccessScreen
import com.pbrlm.abhaya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterRescueScreen(
    navController: NavController,
    viewModel: WaterRescueViewModel = hiltViewModel()
) {
    val form by viewModel.form.collectAsState()

    // Success state
    if (form.submittedRequest != null) {
        SubmissionSuccessScreen(
            request = form.submittedRequest!!,
            onViewRequests = {
                viewModel.clearSubmission()
                navController.navigate(NavRoutes.MyRequests.route) {
                    popUpTo(NavRoutes.EmergencyHome.route)
                }
            },
            onGoHome = {
                viewModel.clearSubmission()
                navController.navigate(NavRoutes.EmergencyHome.route) {
                    popUpTo(NavRoutes.EmergencyHome.route) { inclusive = true }
                }
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌊", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Trapped by Water", fontWeight = FontWeight.Bold, color = TextPrimary)
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

            // Error banner
            if (form.errorMessage != null) {
                ErrorBanner(form.errorMessage!!)
                Spacer(Modifier.height(12.dp))
            }

            // ── People ───────────────────────────────────────────────
            FormSectionHeader("People Details")

            NumberStepper(
                value = form.numberOfPeople,
                onValueChange = viewModel::updateNumberOfPeople,
                label = "Number of People"
            )

            Spacer(Modifier.height(12.dp))

            Text("Vulnerable People Present", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("Children",  form.children,  viewModel::updateChildren,  Modifier.weight(1f))
                ToggleChip("Elderly",   form.elderly,   viewModel::updateElderly,   Modifier.weight(1f))
                ToggleChip("Disabled",  form.disabled,  viewModel::updateDisabled,  Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            Text("Situation", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("People Trapped",   form.peopleTrapped,    viewModel::updatePeopleTrapped, Modifier.weight(1f))
                ToggleChip("Anyone Injured",   form.injured,          viewModel::updateInjured,       Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            ToggleChip(
                "Medical Assistance Required",
                form.medicalRequired,
                viewModel::updateMedicalRequired,
                Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ── Water Details ──────────────────────────────────────
            FormSectionHeader("Water Conditions")

            OptionSelector(
                options   = WaterDepth.values().toList(),
                selected  = form.waterDepth,
                onSelect  = viewModel::updateWaterDepth,
                label     = { it.displayName },
                title     = "Water Depth"
            )

            Spacer(Modifier.height(12.dp))

            Text("Is Water Rising?", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("YES",     form.waterRising == true,  { viewModel.updateWaterRising(true) },  Modifier.weight(1f))
                ToggleChip("NO",      form.waterRising == false, { viewModel.updateWaterRising(false) }, Modifier.weight(1f))
                ToggleChip("UNKNOWN", form.waterRising == null,  { viewModel.updateWaterRising(null) },  Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // ── Location/Contact ──────────────────────────────────
            FormSectionHeader("Contact & Location")

            AbhayaTextField(
                value = form.buildingFloor,
                onValueChange = viewModel::updateBuildingFloor,
                label = "Building / Floor",
                placeholder = "e.g. 2nd Floor, Blue House"
            )

            Spacer(Modifier.height(10.dp))

            AbhayaTextField(
                value = form.contactNumber,
                onValueChange = viewModel::updateContactNumber,
                label = "Contact Number",
                placeholder = "Your mobile number",
                isError = form.contactError.isNotEmpty(),
                errorText = form.contactError
            )

            Spacer(Modifier.height(10.dp))

            AbhayaTextField(
                value = form.additionalInfo,
                onValueChange = viewModel::updateAdditionalInfo,
                label = "Additional Information",
                placeholder = "Describe your situation…",
                singleLine = false,
                maxLines = 4
            )

            Spacer(Modifier.height(16.dp))

            // ── Photo ─────────────────────────────────────────────
            PhotoPickerButton(
                selectedUri = form.photoUri,
                onPhotoSelected = viewModel::updatePhoto
            )

            Spacer(Modifier.height(24.dp))

            // ── Submit ────────────────────────────────────────────
            Button(
                onClick = viewModel::submit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !form.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
            ) {
                if (form.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "SUBMIT WATER RESCUE REQUEST",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "⚠️ $message",
            color = EmergencyRed,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
