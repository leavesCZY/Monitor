package github.leavesczy.monitor.logic

import github.leavesczy.monitor.db.MonitorDetail
import github.leavesczy.monitor.db.MonitorHeader

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 15:41
 * @Desc:
 */
internal data class MonitorDetailPageViewState(
    val title: String,
    val tabTagList: List<String>
)

internal data class MonitorDetailOverviewPageViewState(
    val overview: List<MonitorDetail>
)

internal data class MonitorDetailRequestPageViewState(
    val headers: List<MonitorHeader>,
    val bodyFormat: String
)

internal data class MonitorDetailResponsePageViewState(
    val headers: List<MonitorHeader>,
    val bodyFormat: String
)