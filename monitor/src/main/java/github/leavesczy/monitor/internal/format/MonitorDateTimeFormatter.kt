package github.leavesczy.monitor.internal.format

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object MonitorDateTimeFormatter {

    private const val LIST_PATTERN = "MM-dd HH:mm:ss:SSS"

    private const val DETAIL_PATTERN = "yyyy-MM-dd HH:mm:ss SSS"

    fun formatForList(timestamp: Long): String {
        return format(timestamp = timestamp, pattern = LIST_PATTERN)
    }

    fun formatForDetail(timestamp: Long): String {
        return format(timestamp = timestamp, pattern = DETAIL_PATTERN)
    }

    private fun format(timestamp: Long, pattern: String): String {
        if (timestamp <= 0L) {
            return ""
        }
        val formatter = SimpleDateFormat(pattern, Locale.US)
        return formatter.format(Date(timestamp))
    }

}