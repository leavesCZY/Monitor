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
internal data class MonitorHttpHeader(val name: String, val value: String)

internal enum class MonitorHttpStatus {
    Requested,
    Complete,
    Failed
}

@Entity(tableName = MonitorDatabase.MonitorTableName)
internal data class MonitorHttp(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "host")
    val host: String,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "scheme")
    val scheme: String,
    @ColumnInfo(name = "protocol")
    val protocol: String,
    @ColumnInfo(name = "method")
    val method: String,
    @ColumnInfo(name = "requestHeaders")
    val requestHeaders: List<MonitorHttpHeader>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestDate")
    val requestDate: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorHttpHeader>,
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

    val isSsl: Boolean
        get() = "https".equals(scheme, ignoreCase = true)

    val httpStatus: MonitorHttpStatus
        get() = when {
            error != null -> {
                MonitorHttpStatus.Failed
            }

            responseCode == DEFAULT_RESPONSE_CODE -> {
                MonitorHttpStatus.Requested
            }

            else -> {
                MonitorHttpStatus.Complete
            }
        }

    val notificationText: String
        get() {
            return when (httpStatus) {
                MonitorHttpStatus.Failed -> {
                    "!!!$path"
                }

                MonitorHttpStatus.Requested -> {
                    "...$path"
                }

                else -> {
                    "$responseCode $path"
                }
            }
        }

    val responseSummaryText: String
        get() {
            return when (httpStatus) {
                MonitorHttpStatus.Failed -> {
                    error ?: ""
                }

                MonitorHttpStatus.Requested -> {
                    ""
                }

                else -> {
                    "$responseCode $responseMessage"
                }
            }
        }

    val requestBodyFormat: String
        get() = FormatUtils.formatBody(requestBody, requestContentType)

    val responseBodyFormat: String
        get() = FormatUtils.formatBody(responseBody, responseContentType)

    val responseCodeFormat: String
        get() {
            return when (httpStatus) {
                MonitorHttpStatus.Requested -> {
                    "..."
                }

                MonitorHttpStatus.Complete -> {
                    responseCode.toString()
                }

                MonitorHttpStatus.Failed -> {
                    "!!!"
                }
            }
        }

    val requestDateFormatLong: String
        get() = FormatUtils.getDateFormatLong(requestDate)

    val requestDateFormatShort: String
        get() = FormatUtils.getDateFormatShort(requestDate)

    val responseDateFormatLong: String
        get() = FormatUtils.getDateFormatLong(responseDate)

    val durationFormat: String
        get() {
            val request = requestDate
            val response = responseDate
            if (request <= 0 || response <= 0) {
                return ""
            }
            return when (httpStatus) {
                MonitorHttpStatus.Requested -> {
                    ""
                }

                MonitorHttpStatus.Complete -> {
                    "${response - request} ms"
                }

                MonitorHttpStatus.Failed -> {
                    ""
                }
            }
        }

    val totalSizeFormat: String
        get() {
            return when (httpStatus) {
                MonitorHttpStatus.Requested -> {
                    ""
                }

                MonitorHttpStatus.Complete -> {
                    FormatUtils.formatBytes(requestContentLength + responseContentLength)
                }

                MonitorHttpStatus.Failed -> {
                    ""
                }
            }
        }

    fun getRequestHeadersString(withMarkup: Boolean): String {
        return FormatUtils.formatHeaders(requestHeaders, withMarkup)
    }

    fun getResponseHeadersString(withMarkup: Boolean): String {
        return FormatUtils.formatHeaders(responseHeaders, withMarkup)
    }

}