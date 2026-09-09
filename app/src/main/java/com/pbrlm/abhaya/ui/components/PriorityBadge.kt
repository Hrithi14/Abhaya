package com.pbrlm.abhaya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pbrlm.abhaya.domain.model.EmergencyPriority
import com.pbrlm.abhaya.ui.theme.*

@Composable
fun PriorityBadge(priority: EmergencyPriority, modifier: Modifier = Modifier) {
    val (bg, text) = when (priority) {
        EmergencyPriority.CRITICAL -> CriticalRed to Color.White
        EmergencyPriority.HIGH     -> HighOrange  to Color.White
        EmergencyPriority.MEDIUM   -> MediumAmber to Color(0xFF1A1A1A)
        EmergencyPriority.LOW      -> LowGreen    to Color.White
    }
    Text(
        text = priority.displayName.uppercase(),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        color = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
    )
}
