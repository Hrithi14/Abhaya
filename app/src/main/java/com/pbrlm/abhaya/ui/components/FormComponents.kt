package com.pbrlm.abhaya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pbrlm.abhaya.ui.theme.*

/** Section header used in forms */
@Composable
fun FormSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = EmergencyRed,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/** Outlined text field styled for dark theme */
@Composable
fun AbhayaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorText: String = "",
    placeholder: String = ""
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = { Text(label, color = TextSecondary, fontSize = 13.sp) },
            placeholder   = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = TextDisabled, fontSize = 13.sp) }
            } else null,
            singleLine    = singleLine,
            maxLines      = maxLines,
            isError       = isError,
            modifier      = Modifier.fillMaxWidth(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor       = TextPrimary,
                unfocusedTextColor     = TextPrimary,
                focusedBorderColor     = EmergencyRed,
                unfocusedBorderColor   = DividerColor,
                focusedLabelColor      = EmergencyRed,
                unfocusedLabelColor    = TextSecondary,
                cursorColor            = EmergencyRed,
                errorBorderColor       = EmergencyRed,
                focusedContainerColor  = SurfaceVariant,
                unfocusedContainerColor= SurfaceVariant
            ),
            shape = RoundedCornerShape(10.dp)
        )
        if (isError && errorText.isNotEmpty()) {
            Text(
                text     = errorText,
                color    = EmergencyRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

/** Toggle chip for boolean fields */
@Composable
fun ToggleChip(
    label: String,
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg     = if (selected) EmergencyRed else SurfaceVariant
    val textC  = if (selected) Color.White   else TextSecondary
    val borderC= if (selected) EmergencyRed  else DividerColor

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderC, RoundedCornerShape(8.dp))
            .clickable { onToggle(!selected) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint     = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(3.dp))
        }
        Text(
            text       = label,
            color      = textC,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1
        )
    }
}

/** Number stepper for number of people */
@Composable
fun NumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 1,
    max: Int = 50,
    label: String = "Number of People"
) {
    Column {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Decrease button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .clickable { if (value > min) onValueChange(value - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text       = value.toString(),
                color      = TextPrimary,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.widthIn(min = 36.dp)
            )
            // Increase button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .clickable { if (value < max) onValueChange(value + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Generic option selector rendered as wrap-style chips */
@Composable
fun <T> OptionSelector(
    options: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title, color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        val rows = options.chunked(3)
        rows.forEach { rowItems ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    ToggleChip(
                        label    = label(option),
                        selected = selected == option,
                        onToggle = { if (it) onSelect(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
