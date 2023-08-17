package github.leavesczy.monitor.provider

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.MonitorHttp
import github.leavesczy.monitor.ui.MonitorActivity

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object NotificationProvider {

    private const val CHANNEL_ID = "github.leavesczy.monitor"

    private const val CHANNEL_NAME = "Http Notifications"

    private const val NOTIFICATION_TITLE = "Recording Http Activity"

    private const val NOTIFICATION_ID = 0x20230708

    private const val BUFFER_SIZE = 10

    private val transactionBuffer = LongSparseArray<MonitorHttp>()

    private val notificationManager =
        ContextProvider.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var transactionCount: Int = 0

    @Volatile
    private var showNotification = true

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    @Synchronized
    fun show(monitorHttp: MonitorHttp) {
        if (showNotification) {
            addToBuffer(monitorHttp)
            val builder = NotificationCompat.Builder(ContextProvider.context, CHANNEL_ID)
                .setContentIntent(getContentIntent(ContextProvider.context))
                .setLocalOnly(true)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .setSmallIcon(R.drawable.icon_monitor_launcher)
                .setContentTitle(NOTIFICATION_TITLE)
                .setAutoCancel(false)
            val inboxStyle = NotificationCompat.InboxStyle()
            val size = transactionBuffer.size()
            if (size > 0) {
                builder.setContentText(transactionBuffer.valueAt(size - 1).notificationText)
                for (i in size - 1 downTo 0) {
                    inboxStyle.addLine(transactionBuffer.valueAt(i).notificationText)
                }
            }
            builder.setStyle(inboxStyle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(transactionCount.toString())
            } else {
                builder.setNumber(transactionCount)
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    @Synchronized
    private fun addToBuffer(monitorHttp: MonitorHttp) {
        transactionCount++
        transactionBuffer.put(monitorHttp.id, monitorHttp)
        if (transactionBuffer.size() > BUFFER_SIZE) {
            transactionBuffer.removeAt(0)
        }
    }

    @Synchronized
    fun clearBuffer() {
        transactionBuffer.clear()
        transactionCount = 0
    }

    @Synchronized
    fun dismiss() {
        notificationManager.cancel(NOTIFICATION_ID)
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