package com.pbrlm.abhaya.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.ui.components.PriorityBadge
import com.pbrlm.abhaya.ui.components.StatusBadge
import com.pbrlm.abhaya.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SubmissionSuccessScreen(
    request: EmergencyRequest,
    onViewRequests: () -> Unit,
    onGoHome: () -> Unit
) {
    val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Surface(color = BackgroundDark, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("✅", fontSize = 56.sp)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Request Submitted",
                color = ActionGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Your emergency request has been recorded",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Detail card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SuccessRow("Emergency ID", request.emergencyRequestId)
                    SuccessRow("Type", "${request.emergencyType.emoji} ${request.emergencyType.displayName}")
                    SuccessRow("Time", fmt.format(request.timestamp))
                    SuccessRow("Location",
                        if (request.latitude != 0.0)
                            "${String.format("%.5f", request.latitude)}, ${String.format("%.5f", request.longitude)}"
                        else "GPS Unavailable"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Priority", color = TextSecondary, fontSize = 13.sp)
                        PriorityBadge(priority = request.priority)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status", color = TextSecondary, fontSize = 13.sp)
                        StatusBadge(status = request.status)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "To call emergency services directly, use CALL 112 from the home screen.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onViewRequests,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
            ) {
                Text("VIEW MY REQUESTS", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GO HOME", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SuccessRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.weight(0.4f))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.6f)
        )
    }
}
