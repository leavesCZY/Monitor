package github.leavesczy.monitor.internal.ui.export

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object MonitorShareExporter {

    suspend fun copyText(
        application: Application,
        recordId: Long,
        showToast: suspend (String) -> Unit
    ) {
        runExport(
            application = application,
            recordId = recordId,
            showToast = showToast
        ) { shareText ->
            val label = getString(application = application, resId = R.string.monitor_title)
            val clipboardManager =
                application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(label, shareText))
            showToast(getString(application = application, resId = R.string.monitor_copied))
        }
    }

    suspend fun shareAsText(
        application: Application,
        recordId: Long,
        showToast: suspend (String) -> Unit
    ) {
        runExport(
            application = application,
            recordId = recordId,
            showToast = showToast
        ) { shareText ->
            val label = getString(application = application, resId = R.string.monitor_title)
            val shareIntent = ShareCompat.IntentBuilder(application)
                .setText(shareText)
                .setType("text/plain")
                .setChooserTitle(label)
                .setSubject(label)
                .createChooserIntent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(shareIntent)
        }
    }

    suspend fun shareAsFile(
        application: Application,
        recordId: Long,
        showToast: suspend (String) -> Unit
    ) {
        runExport(
            application = application,
            recordId = recordId,
            showToast = showToast
        ) { shareText ->
            val shareFile = createShareFile(application = application)
            shareFile.writeText(text = shareText, charset = Charsets.UTF_8)
            val authority = application.applicationInfo.packageName + ".MonitorFileProvider"
            val shareFileUri = FileProvider.getUriForFile(application, authority, shareFile)
            val label = getString(application = application, resId = R.string.monitor_title)
            val shareIntent = ShareCompat.IntentBuilder(application)
                .setStream(shareFileUri)
                .setType(application.contentResolver.getType(shareFileUri))
                .setChooserTitle(label)
                .setSubject(label)
                .intent
                .apply {
                    clipData = ClipData.newRawUri(label, shareFileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            val chooserIntent = Intent.createChooser(shareIntent, label)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(chooserIntent)
        }
    }

    private suspend fun runExport(
        application: Application,
        recordId: Long,
        showToast: suspend (String) -> Unit,
        block: suspend (String) -> Unit
    ) {
        runCatching {
            block(queryShareText(recordId = recordId))
        }.onFailure { throwable ->
            throwable.printStackTrace()
            val message = if (throwable is MonitorRecordNotFoundException) {
                getString(application = application, resId = R.string.monitor_record_not_found)
            } else {
                throwable.toString()
            }
            showToast(message)
        }
    }

    private suspend fun queryShareText(recordId: Long): String {
        val record = runCatching {
            MonitorDatabase.instance.monitorDao.queryRecord(id = recordId)
        }.getOrElse { cause ->
            throw MonitorRecordNotFoundException(cause = cause)
        }
        return MonitorOverviewBuilder.buildShareText(record = record)
    }

    private fun createShareFile(application: Application): File {
        val cacheRootDir = File(application.cacheDir, "Monitor")
        cacheRootDir.mkdirs()
        cacheRootDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.startsWith(prefix = "monitor_")) {
                file.delete()
            }
        }
        val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val shareFile = File(cacheRootDir, "monitor_$currentTime.txt")
        shareFile.createNewFile()
        return shareFile
    }

    private fun getString(application: Application, @StringRes resId: Int): String {
        return ContextCompat.getString(application, resId)
    }

    private class MonitorRecordNotFoundException(cause: Throwable) : Exception(cause)

}