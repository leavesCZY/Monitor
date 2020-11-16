package github.leavesc.monitor.viewmodel

import androidx.lifecycle.ViewModel
import github.leavesc.monitor.db.MonitorHttpInformationDatabase

/**
 * 作者：leavesC
 * 时间：2020/10/20 18:37
 * 描述：
 * GitHub：https://github.com/leavesC
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