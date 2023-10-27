package github.leavesczy.monitor.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.MonitorDatabase
import github.leavesczy.monitor.logic.MonitorDetailViewModel
import github.leavesczy.monitor.utils.FormatUtils
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 17:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorDetailsActivity : AppCompatActivity() {

    internal companion object {

        const val KEY_ID = "keyId"

    }

    private val id by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getLongExtra(KEY_ID, 0)
    }

    private val monitorDetailViewModel by viewModels<MonitorDetailViewModel>(factoryProducer = {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MonitorDetailViewModel(id = id) as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitorDetailsPage(
                mainPageViewState = monitorDetailViewModel.mainPageViewState,
                overviewPageViewState = monitorDetailViewModel.overviewPageViewState,
                requestPageViewState = monitorDetailViewModel.requestPageViewState,
                responsePageViewState = monitorDetailViewModel.responsePageViewState,
                onClickBack = ::onClickBack,
                onClickShare = ::onClickShare
            )
        }
    }

    private fun onClickBack() {
        finish()
    }

    private fun onClickShare() {
        lifecycleScope.launch {
            val monitorHttp = MonitorDatabase.instance.monitorDao.query(id = id)
            share(
                context = applicationContext,
                content = FormatUtils.getShareText(monitor = monitorHttp)
            )
        }
    }

    private fun share(context: Context, content: String) {
        val sendIntent = Intent()
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.type = "text/plain"
        val chooserIntent =
            Intent.createChooser(sendIntent, getString(R.string.monitor_library_name))
        if (context !is Activity) {
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }

}