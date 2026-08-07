package github.leavesczy.monitor.internal.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.monitor.internal.db.MonitorHttpHeader
import github.leavesczy.monitor.internal.db.MonitorHttpState
import github.leavesczy.monitor.internal.ui.theme.MonitorTheme

@Composable
internal fun monitorStatusColor(httpState: MonitorHttpState, responseCode: Int): Color {
    return when (httpState) {
        MonitorHttpState.Requesting -> MonitorTheme.colorScheme.secondaryText.color
        MonitorHttpState.Completed -> {
            if (responseCode in 200..299) {
                MonitorTheme.colorScheme.primaryText.color
            } else {
                MonitorTheme.colorScheme.error.color
            }
        }

        MonitorHttpState.Failed -> MonitorTheme.colorScheme.error.color
    }
}

@Composable
internal fun MonitorTitleText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Medium,
        color = color,
        style = LocalTextStyle.current.copy(lineBreak = LineBreak.Paragraph)
    )
}

@Composable
internal fun MonitorSubtitleText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Start,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        color = MonitorTheme.colorScheme.secondaryText.color
    )
}

@Composable
internal fun MonitorPairItem(header: MonitorHttpHeader) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier.weight(weight = 3.1f),
            text = header.name,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
            color = MonitorTheme.colorScheme.primaryText.color
        )
        Text(
            modifier = Modifier.weight(weight = 5f),
            text = header.value,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
            color = MonitorTheme.colorScheme.secondaryText.color
        )
    }
}
