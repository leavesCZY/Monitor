package github.leavesczy.monitor.internal.ui.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import github.leavesczy.monitor.internal.db.MonitorDatabase
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 22:26
 * @Desc:
 */
internal class MonitorViewModel : ViewModel() {

    val monitorPagingDataFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 30,
            prefetchDistance = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MonitorDatabase.instance.monitorDao.queryMonitors()
        },
        remoteMediator = null
    ).flow

    fun onClickClear() {
        viewModelScope.launch {
            MonitorDatabase.instance.monitorDao.deleteAll()
        }
    }

}