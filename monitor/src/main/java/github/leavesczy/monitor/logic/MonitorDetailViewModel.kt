package github.leavesczy.monitor.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.monitor.db.MonitorDatabase
import github.leavesczy.monitor.utils.FormatUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2023/8/17 14:41
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorDetailViewModel(id: Long) : ViewModel() {

    var mainPageViewState by mutableStateOf(
        value = MonitorDetailPageViewState(
            title = "",
            tabTagList = emptyList()
        )
    )
        private set

    var overviewPageViewState by mutableStateOf(
        value = MonitorDetailOverviewPageViewState(
            overview = emptyList()
        )
    )
        private set

    var requestPageViewState by mutableStateOf(
        value = MonitorDetailRequestPageViewState(
            headers = emptyList(),
            bodyFormat = ""
        )
    )
        private set

    var responsePageViewState by mutableStateOf(
        value = MonitorDetailResponsePageViewState(
            headers = emptyList(),
            bodyFormat = ""
        )
    )
        private set

    init {
        viewModelScope.launch {
            val monitorDao = MonitorDatabase.instance.monitorDao
            launch {
                val monitor = monitorDao.query(id = id)
                mainPageViewState = MonitorDetailPageViewState(
                    title = String.format("%s  %s", monitor.method, monitor.pathWithQuery),
                    tabTagList = listOf(
                        "Overview",
                        "Request",
                        "Response"
                    )
                )
            }
            launch {
                monitorDao.queryFlow(id = id)
                    .distinctUntilChanged()
                    .map {
                        MonitorDetailOverviewPageViewState(
                            overview = FormatUtils.buildMonitorOverview(
                                monitor = it
                            )
                        )
                    }
                    .distinctUntilChanged()
                    .collectLatest {
                        overviewPageViewState = it
                    }
            }
            launch {
                monitorDao.queryFlow(id = id)
                    .distinctUntilChanged()
                    .map {
                        MonitorDetailRequestPageViewState(
                            headers = it.requestHeaders,
                            bodyFormat = it.requestBodyFormat
                        )
                    }
                    .distinctUntilChanged()
                    .collectLatest {
                        requestPageViewState = it
                    }
            }
            launch {
                monitorDao.queryFlow(id = id)
                    .distinctUntilChanged()
                    .map {
                        MonitorDetailResponsePageViewState(
                            headers = it.responseHeaders,
                            bodyFormat = it.responseBodyFormat
                        )
                    }
                    .distinctUntilChanged()
                    .collectLatest {
                        responsePageViewState = it
                    }
            }
        }
    }

}