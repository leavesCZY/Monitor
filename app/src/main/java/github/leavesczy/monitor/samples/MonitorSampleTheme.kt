package github.leavesczy.monitor.samples

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val lightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    background = Color(0xFFFAFBFC),
    surface = Color(0xFF2196F3),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFE6EDF3),
    outlineVariant = Color(0xFFD8DEE4),
)

private val darkColorScheme = darkColorScheme(
    primary = Color(0xFF58A6FF),
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    onSurfaceVariant = Color(0xFF8B949E),
    outlineVariant = Color(0xFF30363D),
)

private val typography = Typography(
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

@Composable
internal fun MonitorSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
