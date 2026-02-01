package github.leavesczy.monitor.internal.ui.logic

import androidx.compose.runtime.Stable
import github.leavesczy.monitor.internal.db.MonitorHttpHeader

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 15:41
 * @Desc:
 */
@Stable
internal data class MonitorDetailPageViewState(
    val title: String,
    val tabTagList: List<String>
)

@Stable
internal data class MonitorDetailOverviewPageViewState(
    val overview: List<MonitorHttpHeader>
)

@Stable
internal data class MonitorDetailRequestPageViewState(
    val headers: List<MonitorHttpHeader>,
    val bodyFormatted: String
)

@Stable
internal data class MonitorDetailResponsePageViewState(
    val headers: List<MonitorHttpHeader>,
    val bodyFormatted: String
)