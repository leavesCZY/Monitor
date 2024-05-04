@file:OptIn(DelicateCoroutinesApi::class)

package github.leavesczy.monitor.internal

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.ui.MonitorActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 */
internal object MonitorNotificationHandler {

    private var monitorObserver: Job? = null

    fun init(context: Application) {
        val channelId = context.getString(R.string.monitor_notification_channel_id)
        val channelName = context.getString(R.string.monitor_notification_channel_name)
        val channelDescription =
            context.getString(R.string.monitor_notification_channel_description)
        val notificationTitle = context.getString(R.string.monitor_notification_title)
        val channel = NotificationChannelCompat.Builder(
            channelId,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setName(channelName)
            .setDescription(channelDescription)
            .setSound(null, null)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .setShowBadge(true)
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        monitorObserver?.cancel()
        monitorObserver = GlobalScope.launch(context = Dispatchers.Default) {
            val queryFlow = MonitorDatabase.instance.monitorDao.queryMonitors(limit = 6)
            queryFlow
                .map {
                    it.map { monitor ->
                        monitor.notificationText
                    }
                }
                .distinctUntilChanged()
                .collectLatest {
                    show(
                        context = context,
                        notificationManager = notificationManager,
                        channelId = channelId,
                        notificationTitle = notificationTitle,
                        monitorList = it
                    )
                }
        }
    }

    private fun show(
        context: Context,
        notificationManager: NotificationManager,
        channelId: String,
        notificationTitle: String,
        monitorList: List<String>
    ) {
        val notificationId = 0x20230708
        if (monitorList.isEmpty()) {
            notificationManager.cancel(notificationId)
        } else {
            val builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(notificationTitle)
                .setContentIntent(getContentIntent(context = context))
                .setSmallIcon(R.drawable.monitor_notification_icon)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
            val inboxStyle = NotificationCompat.InboxStyle()
            builder.setContentText(monitorList.first())
            monitorList.forEach {
                inboxStyle.addLine(it)
            }
            builder.setStyle(inboxStyle)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun getContentIntent(context: Context): PendingIntent {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val intent = Intent(context, MonitorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            100,
            intent,
            flag
        )
    }

}