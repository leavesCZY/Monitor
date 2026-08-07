package github.leavesczy.monitor.internal.ui.viewmodel

import androidx.compose.runtime.Stable
import github.leavesczy.monitor.internal.db.MonitorHttpHeader

@Stable
internal data class MonitorDetailViewState(
    val title: String = "",
    val tabs: List<String> = emptyList(),
    val overview: List<MonitorHttpHeader> = emptyList(),
    val requestHeaders: List<MonitorHttpHeader> = emptyList(),
    val requestBodyFormatted: String = "",
    val responseHeaders: List<MonitorHttpHeader> = emptyList(),
    val responseBodyFormatted: String = ""
)