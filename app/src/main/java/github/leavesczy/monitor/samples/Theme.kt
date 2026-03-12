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
    background = Color(0xFFFFFFFF),
    surface = Color(0xFF2196F3),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outlineVariant = Color(0xFFEFF1F3),
)

private val darkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    background = Color(0xFF222222),
    surface = Color(0xFF2196F3),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outlineVariant = Color(0xFF333333),
)

private val typography = Typography(
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
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