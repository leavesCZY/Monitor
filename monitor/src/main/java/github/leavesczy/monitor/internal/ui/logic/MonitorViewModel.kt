@file:OptIn(ExperimentalPagingApi::class)

package github.leavesczy.monitor.internal.ui.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import github.leavesczy.monitor.internal.db.Monitor
import github.leavesczy.monitor.internal.db.MonitorDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 22:26
 * @Desc:
 */
internal class MonitorViewModel : ViewModel() {

    companion object {

        private const val PAGE_SIZE = 20

    }

    fun getMonitors(): Flow<PagingData<Monitor>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 10,
                initialLoadSize = PAGE_SIZE
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