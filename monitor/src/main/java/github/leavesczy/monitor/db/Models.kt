package github.leavesczy.monitor.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import github.leavesczy.monitor.utils.FormatUtils

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Entity(tableName = MonitorDatabase.MonitorTableName)
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
    val requestHeaders: List<MonitorHeader>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestDate")
    val requestDate: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorHeader>,
    @ColumnInfo(name = "responseBody")
    val responseBody: String,
    @ColumnInfo(name = "responseContentType")
    val responseContentType: String,
    @ColumnInfo(name = "responseContentLength")
    val responseContentLength: Long,
    @ColumnInfo(name = "responseDate")
    val responseDate: Long,
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

    val httpStatus by lazy(mode = LazyThreadSafetyMode.NONE) {
        when {
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
    }

    val notificationText by lazy(mode = LazyThreadSafetyMode.NONE) {
        when (httpStatus) {
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
    }

    val responseSummaryText by lazy(mode = LazyThreadSafetyMode.NONE) {
        when (httpStatus) {
            MonitorStatus.Requesting -> {
                ""
            }

            MonitorStatus.Complete -> {
                "$responseCode $responseMessage"
            }

            MonitorStatus.Failed -> {
                error ?: ""
            }
        }
    }

    val pathWithQuery by lazy(mode = LazyThreadSafetyMode.NONE) {
        if (query.isBlank()) {
            path
        } else {
            String.format("%s?%s", path, query)
        }
    }

    val requestBodyFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        FormatUtils.formatBody(requestBody, requestContentType)
    }

    val responseBodyFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        FormatUtils.formatBody(responseBody, responseContentType)
    }

    val responseCodeFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        when (httpStatus) {
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
    }

    val requestDateMDHMS by lazy(mode = LazyThreadSafetyMode.NONE) {
        FormatUtils.getDateMDHMS(date = requestDate)
    }

    val requestDateYMDHMSS by lazy(mode = LazyThreadSafetyMode.NONE) {
        FormatUtils.getDateYMDHMSS(date = requestDate)
    }

    val responseDateYMDHMSS by lazy(mode = LazyThreadSafetyMode.NONE) {
        FormatUtils.getDateYMDHMSS(date = responseDate)
    }

    val requestDurationFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        if (requestDate <= 0 || responseDate <= 0) {
            ""
        } else {
            when (httpStatus) {
                MonitorStatus.Requesting -> {
                    ""
                }

                MonitorStatus.Complete -> {
                    "${responseDate - requestDate} ms"
                }

                MonitorStatus.Failed -> {
                    ""
                }
            }
        }
    }

    val totalSizeFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        when (httpStatus) {
            MonitorStatus.Requesting -> {
                ""
            }

            MonitorStatus.Complete -> {
                FormatUtils.formatBytes(requestContentLength + responseContentLength)
            }

            MonitorStatus.Failed -> {
                ""
            }
        }
    }

}

internal data class MonitorHeader(val name: String, val value: String)

internal enum class MonitorStatus {
    Requesting,
    Complete,
    Failed
}

internal data class MonitorDetail(val header: String, val value: String)