package github.leavesczy.monitor.internal.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import github.leavesczy.monitor.R

/**
 * @Author: leavesCZY
 * @Date: 2025/5/20 15:50
 * @Desc:
 */
internal class MonitorColor(
    day: Color,
    night: Color,
    darkTheme: Boolean
) {

    val color = if (darkTheme) {
        night
    } else {
        day
    }

}

internal data class MonitorColorScheme(
    val darkTheme: Boolean,
    val c_FFFFFFFF_FF101010: MonitorColor = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF101010),
        darkTheme = darkTheme
    ),
    val c_FF0277BD_FF2E3036: MonitorColor = MonitorColor(
        day = Color(color = 0xFF0277BD),
        night = Color(color = 0xFF2E3036),
        darkTheme = darkTheme
    ),
    val c_FFFFFFFF_FFFFFFFF: MonitorColor = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFFFFFFF),
        darkTheme = darkTheme
    ),
    val c_DEFFFFFF_DEFFFFFF: MonitorColor = MonitorColor(
        day = Color(color = 0xDEFFFFFF),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    ),
    val c_B3001018_B3FFFFFF: MonitorColor = MonitorColor(
        day = Color(color = 0xB3001018),
        night = Color(color = 0xB3FFFFFF),
        darkTheme = darkTheme
    ),
    val c_FF001018_DEFFFFFF: MonitorColor = MonitorColor(
        day = Color(color = 0xFF001018),
        night = Color(color = 0xDEFFFFFF),
        darkTheme = darkTheme
    ),
    val c_FFFF545C_FFFA525A: MonitorColor = MonitorColor(
        day = Color(color = 0xFFFF545C),
        night = Color(color = 0xFFFA525A),
        darkTheme = darkTheme
    ),
    val c_FFEFF1F3_FF333333: MonitorColor = MonitorColor(
        day = Color(color = 0xFFEFF1F3),
        night = Color(color = 0xFF333333),
        darkTheme = darkTheme
    )
)

private val LocalMonitorColorScheme = staticCompositionLocalOf<MonitorColorScheme> {
    error("CompositionLocal LocalMonitorColorScheme not present")
}

private val typography = run {
    val robotoFamily = FontFamily(
        Font(R.font.monitor_roboto_regular, FontWeight.Normal),
        Font(R.font.monitor_roboto_medium, FontWeight.Medium)
    )
    Typography(
        displayLarge = TextStyle(
            fontFamily = robotoFamily
        ),
        displayMedium = TextStyle(
            fontFamily = robotoFamily
        ),
        displaySmall = TextStyle(
            fontFamily = robotoFamily
        ),
        headlineLarge = TextStyle(
            fontFamily = robotoFamily
        ),
        headlineMedium = TextStyle(
            fontFamily = robotoFamily
        ),
        headlineSmall = TextStyle(
            fontFamily = robotoFamily
        ),
        titleLarge = TextStyle(
            fontFamily = robotoFamily
        ),
        titleMedium = TextStyle(
            fontFamily = robotoFamily
        ),
        titleSmall = TextStyle(
            fontFamily = robotoFamily
        ),
        bodyLarge = TextStyle(
            fontFamily = robotoFamily
        ),
        bodyMedium = TextStyle(
            fontFamily = robotoFamily
        ),
        bodySmall = TextStyle(
            fontFamily = robotoFamily
        ),
        labelLarge = TextStyle(
            fontFamily = robotoFamily
        ),
        labelMedium = TextStyle(
            fontFamily = robotoFamily
        ),
        labelSmall = TextStyle(
            fontFamily = robotoFamily
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
        if (darkTheme) {
            MonitorColorScheme(darkTheme = true)
        } else {
            MonitorColorScheme(darkTheme = false)
        }
    }
    val customTextSelectionColors = remember {
        TextSelectionColors(
            handleColor = Color(0xFF1BA2E6),
            backgroundColor = Color(0x661BA2E6)
        )
    }
    val localContext = LocalContext.current
    val density = remember {
        Density(
            density = localContext.resources.displayMetrics.widthPixels / 380f,
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