package github.leavesczy.monitor.internal.ui.theme

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

/**
 * Unified palette for Monitor UI.
 * Light: soft paper + ink. Dark: GitHub-like neutrals.
 * JSON: restrained 4-role syntax colors (key / string / literal / structure).
 */
@Stable
internal data class MonitorColorScheme(private val darkTheme: Boolean) {
    val background = MonitorColor(
        day = Color(color = 0xFFFAFBFC),
        night = Color(color = 0xFF0D1117),
        darkTheme = darkTheme
    )
    val topBar = MonitorColor(
        day = Color(color = 0xFF2196F3),
        night = Color(color = 0xFF161B22),
        darkTheme = darkTheme
    )
    val tabIndicator = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFF58A6FF),
        darkTheme = darkTheme
    )
    val onTopBar = MonitorColor(
        day = Color(color = 0xFFFFFFFF),
        night = Color(color = 0xFFF0F3F6),
        darkTheme = darkTheme
    )
    val primaryText = MonitorColor(
        day = Color(color = 0xFF1F2328),
        night = Color(color = 0xFFE6EDF3),
        darkTheme = darkTheme
    )
    val secondaryText = MonitorColor(
        day = Color(color = 0xFF656D76),
        night = Color(color = 0xFF8B949E),
        darkTheme = darkTheme
    )
    val success = MonitorColor(
        day = Color(color = 0xFF1A7F37),
        night = Color(color = 0xFF3FB950),
        darkTheme = darkTheme
    )
    val error = MonitorColor(
        day = Color(color = 0xFFCF222E),
        night = Color(color = 0xFFF85149),
        darkTheme = darkTheme
    )
    val divider = MonitorColor(
        day = Color(color = 0xFFD8DEE4),
        night = Color(color = 0xFF30363D),
        darkTheme = darkTheme
    )
    val jsonKey = MonitorColor(
        day = Color(color = 0xFF0550AE),
        night = Color(color = 0xFF79C0FF),
        darkTheme = darkTheme
    )
    val jsonValue = MonitorColor(
        day = Color(color = 0xFF116329),
        night = Color(color = 0xFF7EE787),
        darkTheme = darkTheme
    )
    val jsonLiteral = MonitorColor(
        day = Color(color = 0xFF953800),
        night = Color(color = 0xFFFFA657),
        darkTheme = darkTheme
    )
    val jsonBrace = MonitorColor(
        day = Color(color = 0xFF24292F),
        night = Color(color = 0xFFC9D1D9),
        darkTheme = darkTheme
    )
    val jsonPunctuation = MonitorColor(
        day = Color(color = 0xFF6E7781),
        night = Color(color = 0xFF8B949E),
        darkTheme = darkTheme
    )
}

private val LocalMonitorColorScheme = staticCompositionLocalOf<MonitorColorScheme> {
    error("CompositionLocal LocalMonitorColorScheme not present")
}

private val typography = Typography(
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif)
)

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
            handleColor = Color(color = 0xFF1A73E8),
            backgroundColor = Color(color = 0x331A73E8)
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
