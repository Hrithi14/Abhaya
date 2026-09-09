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
import com.pbrlm.abhaya.domain.model.EmergencyStatus
import com.pbrlm.abhaya.ui.theme.*

@Composable
fun StatusBadge(status: EmergencyStatus, modifier: Modifier = Modifier) {
    val (bg, text) = when (status) {
        EmergencyStatus.ACTIVE            -> StatusActive       to Color.White
        EmergencyStatus.ACKNOWLEDGED      -> StatusAcknowledged to Color.White
        EmergencyStatus.RESCUE_IN_PROGRESS-> StatusRescue       to Color.White
        EmergencyStatus.RESOLVED          -> StatusResolved     to Color.White
        EmergencyStatus.CANCELLED         -> StatusCancelled    to Color.White
        EmergencyStatus.FALSE_REPORT      -> StatusCancelled    to Color.White
    }
    Text(
        text = status.displayName.uppercase(),
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
