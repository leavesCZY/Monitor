package github.leavesczy.monitor.internal.ui.logic

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.db.buildOverview
import github.leavesczy.monitor.internal.db.buildShareText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 22:49
 * @Desc:
 */
internal class MonitorDetailsViewModel(
    private val application: Application,
    private val monitorId: Long
) : ViewModel() {

    var mainPageViewState by mutableStateOf(
        value = MonitorDetailPageViewState(
            title = "",
            tabTagList = emptyList()
        )
    )

    var overviewPageViewState by mutableStateOf(
        value = MonitorDetailOverviewPageViewState(
            overview = emptyList()
        )
    )

    var requestPageViewState by mutableStateOf(
        value = MonitorDetailRequestPageViewState(
            headers = emptyList(),
            formattedBody = ""
        )
    )

    var responsePageViewState by mutableStateOf(
        value = MonitorDetailResponsePageViewState(
            headers = emptyList(),
            formattedBody = ""
        )
    )

    init {
        viewModelScope.launch {
            MonitorDatabase.instance.monitorDao.queryMonitorAsFlow(id = monitorId)
                .distinctUntilChanged()
                .collectLatest {
                    mainPageViewState = MonitorDetailPageViewState(
                        title = it.method + " " + it.pathWithQuery,
                        tabTagList = listOf(
                            application.getString(R.string.monitor_overview),
                            application.getString(R.string.monitor_request),
                            application.getString(R.string.monitor_response),
                        )
                    )
                    overviewPageViewState = MonitorDetailOverviewPageViewState(
                        overview = it.buildOverview()
                    )
                    requestPageViewState = MonitorDetailRequestPageViewState(
                        headers = it.requestHeaders,
                        formattedBody = it.requestBodyFormat
                    )
                    responsePageViewState = MonitorDetailResponsePageViewState(
                        headers = it.responseHeaders,
                        formattedBody = it.responseBodyFormat
                    )
                }
        }
    }

    fun copyText() {
        viewModelScope.launch(context = Dispatchers.Default) {
            try {
                val shareText = queryMonitorShareText()
                val monitor = application.getString(R.string.monitor_monitor)
                val clipboardManager =
                    application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(monitor, shareText)
                clipboardManager.setPrimaryClip(clipData)
                showToast(resId = R.string.monitor_copied)
            } catch (e: Throwable) {
                e.printStackTrace()
                showToast(msg = e.toString())
            }
        }
    }

    fun shareAsText() {
        viewModelScope.launch(context = Dispatchers.Default) {
            try {
                val shareText = queryMonitorShareText()
                val monitor = application.getString(R.string.monitor_monitor)
                val shareIntent = ShareCompat.IntentBuilder(application)
                    .setText(shareText)
                    .setType("text/plain")
                    .setChooserTitle(monitor)
                    .setSubject(monitor)
                    .createChooserIntent()
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(shareIntent)
            } catch (e: Throwable) {
                e.printStackTrace()
                showToast(msg = e.toString())
            }
        }
    }

    fun shareAsFile() {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val shareText = queryMonitorShareText()
                val shareFile = createShareFile()
                shareFile.writeText(text = shareText, charset = Charsets.UTF_8)
                val authority = application.applicationInfo.packageName + ".monitor.file.provider"
                val shareFileUri =
                    FileProvider.getUriForFile(application, authority, shareFile)
                val monitor = application.getString(R.string.monitor_monitor)
                val shareIntent = ShareCompat.IntentBuilder(application)
                    .setStream(shareFileUri)
                    .setType(application.contentResolver.getType(shareFileUri))
                    .setChooserTitle(monitor)
                    .setSubject(monitor)
                    .intent
                shareIntent.apply {
                    clipData = ClipData.newRawUri(monitor, shareFileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooserIntent = Intent.createChooser(shareIntent, monitor)
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(chooserIntent)
            } catch (e: Throwable) {
                e.printStackTrace()
                showToast(msg = e.toString())
            }
        }
    }

    private fun createShareFile(): File {
        val cacheRootDir = File(application.cacheDir, "monitor")
        cacheRootDir.mkdirs()
        val currentTime =
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val shareFile = File(cacheRootDir, "monitor_$currentTime.txt")
        shareFile.createNewFile()
        return shareFile
    }

    private suspend fun queryMonitorShareText(): String {
        return MonitorDatabase.instance.monitorDao.queryMonitor(id = monitorId).buildShareText()
    }

    private suspend fun showToast(@StringRes resId: Int) {
        showToast(msg = application.getString(resId))
    }

    private suspend fun showToast(msg: String) {
        withContext(context = Dispatchers.Main.immediate) {
            Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
        }
    }

}