package github.leavesczy.monitor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import github.leavesczy.monitor.logic.MonitorDetailOverviewPageViewState

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:13
 * @Desc:
 */
@Composable
internal fun MonitorDetailsOverviewPage(pageViewState: MonitorDetailOverviewPageViewState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = LazyColumnPadding
    ) {
        items(
            items = pageViewState.overview,
            contentType = {
                "MonitorOverviewItem"
            }
        ) {
            MonitorPairItem(key = it.header, value = it.value)
        }
    }
}