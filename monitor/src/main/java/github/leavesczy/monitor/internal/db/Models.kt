package github.leavesczy.monitor.internal.db

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 */
@Stable
@Entity(tableName = MonitorDatabase.MONITOR_TABLE_NAME)
internal data class Monitor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "scheme")
    val scheme: String,
    @ColumnInfo(name = "host")
    val host: String,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "protocol")
    val protocol: String,
    @ColumnInfo(name = "method")
    val method: String,
    @ColumnInfo(name = "requestHeaders")
    val requestHeaders: List<MonitorPair>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String?,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestTime")
    val requestTime: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorPair>,
    @ColumnInfo(name = "responseBody")
    val responseBody: String?,
    @ColumnInfo(name = "responseContentType")
    val responseContentType: String,
    @ColumnInfo(name = "responseContentLength")
    val responseContentLength: Long,
    @ColumnInfo(name = "responseTime")
    val responseTime: Long,
    @ColumnInfo(name = "responseTlsVersion")
    val responseTlsVersion: String,
    @ColumnInfo(name = "responseCipherSuite")
    val responseCipherSuite: String,
    @ColumnInfo(name = "responseCode")
    val responseCode: Int = DEFAULT_RESPONSE_CODE,
    @ColumnInfo(name = "responseMessage")
    val responseMessage: String,
    @ColumnInfo(name = "error")
    val error: String?
) {

    companion object {

        private const val DEFAULT_RESPONSE_CODE = -1024

    }

    val httpStatus: MonitorStatus
        get() = when {
            error != null -> {
                MonitorStatus.Failed
            }

            responseCode == DEFAULT_RESPONSE_CODE -> {
                MonitorStatus.Requesting
            }

            else -> {
                MonitorStatus.Complete
            }
        }

    val notificationText: String
        get() = when (httpStatus) {
            MonitorStatus.Requesting -> {
                "...$pathWithQuery"
            }

            MonitorStatus.Complete -> {
                "$responseCode $pathWithQuery"
            }

            MonitorStatus.Failed -> {
                "!!!$pathWithQuery"
            }
        }

    val pathWithQuery by lazy(mode = LazyThreadSafetyMode.NONE) {
        val httpUrl = url.toHttpUrl()
        val path = httpUrl.pathSegments.joinToString("/")
        if (query.isBlank()) {
            path
        } else {
            "$path?$query"
        }
    }

    val urlFormatted by lazy(mode = LazyThreadSafetyMode.NONE) {
        "$scheme://$host/$pathWithQuery"
    }

    val requestBodyFormatted by lazy(mode = LazyThreadSafetyMode.NONE) {
        formatBody(requestBody, requestContentType)
    }

    val responseBodyFormatted by lazy(mode = LazyThreadSafetyMode.NONE) {
        formatBody(responseBody, responseContentType)
    }

    val responseCodeFormatted: String
        get() = when (httpStatus) {
            MonitorStatus.Requesting -> {
                "..."
            }

            MonitorStatus.Complete -> {
                responseCode.toString()
            }

            MonitorStatus.Failed -> {
                "!!!"
            }
        }

    val requestTimeFormatted: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.getDefault())
        simpleDateFormat.format(Date(requestTime))
    }

    val requestDurationFormatted by lazy(mode = LazyThreadSafetyMode.NONE) {
        if (requestTime <= 0 || responseTime <= 0) {
            ""
        } else {
            when (httpStatus) {
                MonitorStatus.Requesting -> {
                    ""
                }

                MonitorStatus.Complete -> {
                    "${responseTime - requestTime} ms"
                }

                MonitorStatus.Failed -> {
                    ""
                }
            }
        }
    }

    val totalSizeFormatted by lazy(mode = LazyThreadSafetyMode.NONE) {
        when (httpStatus) {
            MonitorStatus.Requesting -> {
                ""
            }

            MonitorStatus.Complete -> {
                formatBytes(bytes = requestContentLength + responseContentLength)
            }

            MonitorStatus.Failed -> {
                ""
            }
        }
    }

}

@Stable
internal data class MonitorPair(
    val name: String,
    val value: String
)

@Stable
internal enum class MonitorStatus {
    Requesting,
    Complete,
    Failed
}