package github.leavesc.monitor

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import github.leavesc.monitor.db.HttpInformation
import github.leavesc.monitor.db.MonitorHttpInformationDatabase
import github.leavesc.monitor.holder.NotificationHolder
import github.leavesc.monitor.ui.MonitorActivity
import kotlin.concurrent.thread

/**
 * 作者：leavesC
 * 时间：2020/10/20 18:26
 * 描述：
 * GitHub：https://github.com/leavesC
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