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
    val requestHeaders: List<MonitorHttpHeader>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String?,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestTime")
    val requestTime: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorHttpHeader>,
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

        private const val DEFAULT_RESPONSE_CODE = -1

    }

    val httpState: MonitorHttpState
        get() = when {
            error != null -> {
                MonitorHttpState.Failed
            }

            responseCode == DEFAULT_RESPONSE_CODE -> {
                MonitorHttpState.Requesting
            }

            else -> {
                MonitorHttpState.Complete
            }
        }

    val notificationText: String
        get() = when (httpState) {
            MonitorHttpState.Requesting -> {
                "...$pathWithQuery"
            }

            MonitorHttpState.Complete -> {
                "$responseCode $pathWithQuery"
            }

            MonitorHttpState.Failed -> {
                "!!!$pathWithQuery"
            }
        }

    val pathWithQuery by lazy {
        val httpUrl = url.toHttpUrl()
        val path = httpUrl.pathSegments.joinToString("/")
        if (query.isBlank()) {
            path
        } else {
            "$path?$query"
        }
    }

    val urlFormatted by lazy {
        "$scheme://$host/$pathWithQuery"
    }

    val requestBodyFormatted by lazy {
        MonitorUtils.formatBody(requestBody, requestContentType)
    }

    val responseBodyFormatted by lazy {
        MonitorUtils.formatBody(responseBody, responseContentType)
    }

    val responseCodeFormatted: String
        get() = when (httpState) {
            MonitorHttpState.Requesting -> {
                "..."
            }

            MonitorHttpState.Complete -> {
                responseCode.toString()
            }

            MonitorHttpState.Failed -> {
                "!!!"
            }
        }

    val requestTimeFormatted: String by lazy {
        val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.US)
        simpleDateFormat.format(Date(requestTime))
    }

    val requestDurationFormatted by lazy {
        if (requestTime <= 0 || responseTime <= 0) {
            ""
        } else {
            when (httpState) {
                MonitorHttpState.Requesting -> {
                    ""
                }

                MonitorHttpState.Complete -> {
                    "${responseTime - requestTime} ms"
                }

                MonitorHttpState.Failed -> {
                    ""
                }
            }
        }
    }

    val totalSizeFormatted by lazy {
        when (httpState) {
            MonitorHttpState.Requesting -> {
                ""
            }

            MonitorHttpState.Complete -> {
                MonitorUtils.formatBytes(bytes = requestContentLength + responseContentLength)
            }

            MonitorHttpState.Failed -> {
                ""
            }
        }
    }

}

@Stable
internal data class MonitorHttpHeader(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "value") val value: String
)

@Stable
internal enum class MonitorHttpState {
    Requesting,
    Complete,
    Failed;
}