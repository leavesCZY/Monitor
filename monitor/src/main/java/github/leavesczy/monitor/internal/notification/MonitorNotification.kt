package github.leavesczy.monitor.internal.notification

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.core.MonitorScope
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.ui.MonitorActivity
import github.leavesczy.monitor.internal.ui.model.notificationText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal object MonitorNotification {

    private const val NOTIFICATION_ID = 20260708

    @Volatile
    private var isInitialized = false

    private var recordObserver: Job? = null

    fun initialize(context: Application) {
        if (isInitialized) {
            return
        }
        synchronized(lock = MonitorNotification::class.java) {
            if (isInitialized) {
                return
            }
            createNotificationChannel(context = context)
            observeRecentRecords(context = context)
            isInitialized = true
        }
    }

    private fun createNotificationChannel(context: Application) {
        val channelId =
            getString(context = context, resId = R.string.monitor_notification_channel_id)
        val channel = NotificationChannelCompat.Builder(
            channelId,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setName(getString(context = context, resId = R.string.monitor_notification_channel_name))
            .setDescription(
                getString(
                    context = context,
                    resId = R.string.monitor_notification_channel_description
                )
            )
            .setSound(null, null)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .setShowBadge(true)
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    private fun observeRecentRecords(context: Application) {
        val channelId =
            getString(context = context, resId = R.string.monitor_notification_channel_id)
        val notificationTitle =
            getString(context = context, resId = R.string.monitor_notification_title)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        recordObserver?.cancel()
        recordObserver = MonitorScope.io.launch(context = Dispatchers.Default) {
            MonitorDatabase.instance.monitorDao.queryRecords(limit = 7)
                .map { records ->
                    records.map { record ->
                        record.notificationText
                    }
                }
                .distinctUntilChanged()
                .collectLatest { recordLines ->
                    showNotification(
                        context = context,
                        notificationManager = notificationManager,
                        channelId = channelId,
                        notificationTitle = notificationTitle,
                        recordLines = recordLines
                    )
                }
        }
    }

    private fun showNotification(
        context: Context,
        notificationManager: NotificationManager,
        channelId: String,
        notificationTitle: String,
        recordLines: List<String>
    ) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }
        if (recordLines.isEmpty()) {
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(notificationTitle)
        recordLines.drop(n = 1).forEach { line ->
            inboxStyle.addLine(line)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.monitor_notification_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(notificationTitle)
            .setContentText(recordLines.first())
            .setContentIntent(buildContentIntent(context = context))
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setStyle(inboxStyle)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MonitorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getString(context: Context, resId: Int): String {
        return ContextCompat.getString(context, resId)
    }

}