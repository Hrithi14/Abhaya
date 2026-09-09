package com.pbrlm.abhaya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AbhayaDarkColorScheme = darkColorScheme(
    primary            = EmergencyRed,
    onPrimary          = Color.White,
    primaryContainer   = EmergencyRedDark,
    onPrimaryContainer = Color.White,
    secondary          = ActionBlue,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFF1A237E),
    onSecondaryContainer = Color.White,
    tertiary           = ActionGreen,
    onTertiary         = Color.White,
    background         = BackgroundDark,
    onBackground       = TextPrimary,
    surface            = SurfaceDark,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    outline            = DividerColor,
    error              = EmergencyRed,
    onError            = Color.White
)

@Composable
fun AbhayaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AbhayaDarkColorScheme,
        typography  = AbhayaTypography,
        content     = content
    )
}
