package github.leavesczy.monitor.ui

import androidx.compose.runtime.Composable
import github.leavesczy.monitor.logic.MonitorDetailRequestPageViewState

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:13
 * @Desc:
 */
@Composable
internal fun MonitorDetailsRequestPage(pageViewState: MonitorDetailRequestPageViewState) {
    MonitorDetailsPage(
        headers = pageViewState.headers,
        bodyFormat = pageViewState.bodyFormat
    )
}