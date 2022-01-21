package github.leavesczy.monitor

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import github.leavesczy.monitor.db.HttpInformation
import github.leavesczy.monitor.db.MonitorHttpInformationDatabase
import github.leavesczy.monitor.holder.NotificationHolder
import github.leavesczy.monitor.ui.MonitorActivity
import kotlin.concurrent.thread

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:26
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object Monitor {

    fun getLaunchIntent(context: Context): Intent {
        val intent = Intent(context, MonitorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun clearCache() {
        thread {
            MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.deleteAll()
        }
    }

    fun queryAllRecord(limit: Int): LiveData<List<HttpInformation>> {
        return MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.queryAllRecordObservable(
            limit
        )
    }

    fun clearNotification() {
        NotificationHolder.clearBuffer()
        NotificationHolder.dismiss()
    }

    fun showNotification(showNotification: Boolean) {
        NotificationHolder.showNotification(showNotification)
    }

    fun queryAllRecord(): LiveData<List<HttpInformation>> {
        return MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.queryAllRecordObservable()
    }

}