package github.leavesczy.monitor.viewmodel

import androidx.lifecycle.ViewModel
import github.leavesczy.monitor.db.MonitorHttpInformationDatabase

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:37
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorViewModel : ViewModel() {

    companion object {

        private const val LIMIT = 300

    }

    val allRecordLiveData by lazy {
        MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.queryAllRecordObservable(
            LIMIT
        )
    }

    fun init() {

    }

}