package github.leavesczy.monitor.viewmodel

import androidx.lifecycle.ViewModel
import github.leavesczy.monitor.db.MonitorHttpInformationDatabase

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:37
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorDetailViewModel(id: Long) : ViewModel() {

    fun init() {

    }

    val recordLiveData by lazy {
        MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.queryRecordObservable(id)
    }

    fun queryRecordById() {

    }

}