package github.leavesczy.monitor.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import github.leavesczy.monitor.db.Monitor
import github.leavesczy.monitor.db.MonitorDatabase
import github.leavesczy.monitor.provider.MonitorNotificationHandler
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:58
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val httpRecordFlow =
                MonitorDatabase.instance.monitorDao.queryFlow(limit = 300)
            val dataList by httpRecordFlow.collectAsState(initial = emptyList())
            MonitorPage(
                onClickBack = ::onClickBack,
                onClickClear = ::onClickClear,
                dataList = dataList,
                onClickMonitorItem = ::onClickMonitorItem
            )
        }
    }

    private fun onClickBack() {
        finish()
    }

    private fun onClickClear() {
        lifecycleScope.launch {
            MonitorDatabase.instance.monitorDao.deleteAll()
            MonitorNotificationHandler.clearBuffer()
            MonitorNotificationHandler.dismiss()
        }
    }

    private fun onClickMonitorItem(monitor: Monitor) {
        val intent = Intent(this, MonitorDetailsActivity::class.java)
        intent.putExtra(MonitorDetailsActivity.KEY_ID, monitor.id)
        startActivity(intent)
    }

}