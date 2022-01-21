package github.leavesczy.monitor.holder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import github.leavesczy.monitor.Monitor.getLaunchIntent
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.HttpInformation
import github.leavesczy.monitor.service.ClearMonitorService

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object NotificationHolder {

    private const val CHANNEL_ID = "monitorLeavesChannelId"

    private const val CHANNEL_NAME = "Http Notifications"

    private const val NOTIFICATION_TITLE = "Recording Http Activity"

    private const val NOTIFICATION_ID = 19950724

    private const val BUFFER_SIZE = 10

    private val transactionBuffer = LongSparseArray<HttpInformation>()

    private val context: Context
        get() = ContextHolder.context

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var transactionCount: Int = 0

    @Volatile
    private var showNotification = true

    private val clearAction: NotificationCompat.Action
        get() {
            val intent = PendingIntent.getService(
                context, 200,
                Intent(context, ClearMonitorService::class.java), PendingIntent.FLAG_ONE_SHOT
            )
            return NotificationCompat.Action(R.drawable.icon_monitor_launcher, "Clear", intent)
        }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN
                )
            )
        }
    }

    @Synchronized
    fun show(transaction: HttpInformation) {
        if (showNotification) {
            addToBuffer(transaction)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(getContentIntent(context))
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
    private fun addToBuffer(httpInformation: HttpInformation) {
        transactionCount++
        transactionBuffer.put(httpInformation.id, httpInformation)
        if (transactionBuffer.size() > BUFFER_SIZE) {
            transactionBuffer.removeAt(0)
        }
    }

    @Synchronized
    fun showNotification(showNotification: Boolean) {
        this.showNotification = showNotification
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
        return PendingIntent.getActivity(context, 100, getLaunchIntent(context), 0)
    }

}