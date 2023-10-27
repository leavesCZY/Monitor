package github.leavesczy.monitor.ui

import androidx.compose.runtime.Composable
import github.leavesczy.monitor.logic.MonitorDetailResponsePageViewState

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:14
 * @Desc:
 */
@Composable
internal fun MonitorDetailsResponsePage(pageViewState: MonitorDetailResponsePageViewState) {
    MonitorDetailsPage(
        headers = pageViewState.headers,
        bodyFormat = pageViewState.bodyFormat
    )
}