package github.leavesc.monitor.service

import android.app.IntentService
import android.content.Intent
import github.leavesc.monitor.Monitor

/**
 * 作者：leavesC
 * 时间：2020/11/8 14:44
 * 描述：
 * GitHub：https://github.com/leavesC
 */
internal class ClearMonitorService : IntentService(ClearMonitorService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        Monitor.clearNotification()
    }

}