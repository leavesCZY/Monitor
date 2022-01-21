package github.leavesczy.monitor.service

import android.app.IntentService
import android.content.Intent
import github.leavesczy.monitor.Monitor

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class ClearMonitorService : IntentService(ClearMonitorService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        Monitor.clearNotification()
    }

}