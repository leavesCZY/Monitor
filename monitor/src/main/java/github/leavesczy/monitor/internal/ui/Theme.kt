package github.leavesczy.monitor.internal.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density

/**
 * @Author: leavesCZY
 * @Date: 2025/5/20 15:50
 * @Desc:
 */
@Stable
internal data class MonitorColor(
    private val day: Color,
    private val night: Color,
    private val darkTheme: Boolean
) {

    val color = if (darkTheme) {
        night
    } else {
        day
    }

}

@Stable
internal data class MonitorColorScheme(private val darkTheme: Boolean) {
    val c_FFFFFFFF_FF101010 = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF101010),
        darkTheme = darkTheme
    )
    val c_FF2196F3_FF2E3036 = MonitorColor(
        day = Color(color = 0xFF2196F3),
        night = Color(color = 0xFF2E3036),
        darkTheme = darkTheme
    )
    val c_FF8EBBEA_660085EB = MonitorColor(
        day = Color(color = 0xFF8EBBEA),
        night = Color(color = 0x660085EB),
        darkTheme = darkTheme
    )
    val c_FFFFFFFF_FFFFFFFF = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    )
    val c_DEFFFFFF_DEFFFFFF = MonitorColor(
        day = Color(color = 0xDEFFFFFF),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    )
    val c_B3001018_B3FFFFFF = MonitorColor(
        day = Color(color = 0xB3001018),
        night = Color(color = 0xB3FFFFFF),
        darkTheme = darkTheme
    )
    val c_FF001018_DEFFFFFF = MonitorColor(
        day = Color(color = 0xFF001018),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    )
    val c_FFFF545C_FFFA525A = MonitorColor(
        day = Color(color = 0xFFFF545C),
        night = Color(color = 0xFFFA525A),
        darkTheme = darkTheme
    )
    val c_FFEFF1F3_FF333333 = MonitorColor(
        day = Color(color = 0xFFEFF1F3),
        night = Color(color = 0xFF333333),
        darkTheme = darkTheme
    )
}

private val LocalMonitorColorScheme = staticCompositionLocalOf<MonitorColorScheme> {
    error("CompositionLocal LocalMonitorColorScheme not present")
}

private val typography = run {
    val fontFamily = FontFamily.SansSerif
    Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily
        )
    )
}

internal object MonitorTheme {

    val colorScheme: MonitorColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalMonitorColorScheme.current

}

@Composable
internal fun MonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val monitorColorScheme = remember(key1 = darkTheme) {
        MonitorColorScheme(darkTheme = darkTheme)
    }
    val customTextSelectionColors = remember {
        TextSelectionColors(
            handleColor = Color(color = 0xFF1BA2E6),
            backgroundColor = Color(color = 0x661BA2E6)
        )
    }
    val localResources = LocalResources.current
    val density = remember {
        Density(
            density = localResources.displayMetrics.widthPixels / 380f,
            fontScale = 1f
        )
    }
    MaterialTheme(
        typography = typography,
        content = {
            CompositionLocalProvider(
                LocalMonitorColorScheme provides monitorColorScheme,
                LocalTextSelectionColors provides customTextSelectionColors,
                LocalDensity provides density,
                content = content
            )
        }
    )
}