package github.leavesczy.monitor.logic

import androidx.lifecycle.ViewModel
import github.leavesczy.monitor.db.MonitorDatabase

/**
 * @Author: leavesCZY
 * @Date: 2023/8/17 14:41
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorViewModel : ViewModel() {

    val allRecordLiveData by lazy(mode = LazyThreadSafetyMode.NONE) {
        MonitorDatabase.instance.monitorDao.queryRecord(limit = 400)
    }

}