package github.leavesc.monitor.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import github.leavesc.monitor.utils.FormatUtils
import okhttp3.Headers

/**
 * 作者：leavesC
 * 时间：2020/11/8 14:43
 * 描述：
 * GitHub：https://github.com/leavesC
 */
data class HttpHeader(val name: String, val value: String)

@Entity(tableName = "monitor_httpInformation")
class HttpInformation {

    companion object {

        private const val DEFAULT_RESPONSE_CODE = -100
    }

    enum class Status {
        Requested, Complete, Failed
    }

    @PrimaryKey(autoGenerate = true)
    var id = 0L

    var url = ""
    var host = ""
    var path = ""
    var scheme = ""
    var protocol = ""
    var method = ""

    var requestHeaders = mutableListOf<HttpHeader>()
    var responseHeaders = mutableListOf<HttpHeader>()

    var requestBody = ""
    var requestContentType = ""
    var requestContentLength = 0L

    var responseBody = ""
    var responseContentType = ""
    var responseContentLength = 0L

    var requestDate = 0L
    var responseDate = 0L

    var responseTlsVersion = ""
    var responseCipherSuite = ""

    var responseCode = DEFAULT_RESPONSE_CODE
    var responseMessage = ""

    var error: String? = null

    val status: Status
        get() = when {
            error != null -> Status.Failed
            responseCode == DEFAULT_RESPONSE_CODE -> Status.Requested
            else -> Status.Complete
        }

    val notificationText: String
        get() {
            return when (status) {
                Status.Failed -> "!!!$path"
                Status.Requested -> "...$path"
                else -> "$responseCode $path"
            }
        }

    val responseSummaryText: String
        get() {
            return when (status) {
                Status.Failed -> error ?: ""
                Status.Requested -> ""
                else -> "$responseCode $responseMessage"
            }
        }

    val requestBodyFormat: String
        get() = FormatUtils.formatBody(requestBody, requestContentType)

    val responseBodyFormat: String
        get() = FormatUtils.formatBody(responseBody, responseContentType)

    val responseCodeFormat: String
        get() {
            return when (status) {
                Status.Requested -> "..."
                Status.Complete -> responseCode.toString()
                Status.Failed -> "!!!"
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
            return when (status) {
                Status.Requested -> ""
                Status.Complete -> "${response - request} ms"
                Status.Failed -> ""
            }
        }

    val totalSizeFormat: String
        get() {
            return when (status) {
                Status.Requested -> ""
                Status.Complete -> FormatUtils.formatBytes(requestContentLength + responseContentLength)
                Status.Failed -> ""
            }
        }

    val isSsl: Boolean
        get() = "https".equals(scheme, ignoreCase = true)

    fun setRequestHttpHeaders(headers: Headers?) {
        requestHeaders.clear()
        if (headers != null && headers.size > 0) {
            headers.forEach {
                requestHeaders.add(HttpHeader(it.first, it.second))
            }
            requestHeaders
        }
    }

    fun setResponseHttpHeaders(headers: Headers?) {
        responseHeaders.clear()
        if (headers != null && headers.size > 0) {
            headers.forEach {
                responseHeaders.add(HttpHeader(it.first, it.second))
            }
        }
    }

    fun getRequestHeadersString(withMarkup: Boolean): String {
        return FormatUtils.formatHeaders(requestHeaders, withMarkup)
    }

    fun getResponseHeadersString(withMarkup: Boolean): String {
        return FormatUtils.formatHeaders(responseHeaders, withMarkup)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HttpInformation
        if (id != other.id) return false
        if (url != other.url) return false
        if (host != other.host) return false
        if (path != other.path) return false
        if (scheme != other.scheme) return false
        if (protocol != other.protocol) return false
        if (method != other.method) return false
        if (requestHeaders != other.requestHeaders) return false
        if (responseHeaders != other.responseHeaders) return false
        if (requestBody != other.requestBody) return false
        if (requestContentType != other.requestContentType) return false
        if (requestContentLength != other.requestContentLength) return false
        if (responseBody != other.responseBody) return false
        if (responseContentType != other.responseContentType) return false
        if (responseContentLength != other.responseContentLength) return false
        if (requestDate != other.requestDate) return false
        if (responseDate != other.responseDate) return false
        if (responseTlsVersion != other.responseTlsVersion) return false
        if (responseCipherSuite != other.responseCipherSuite) return false
        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (error != other.error) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + host.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + scheme.hashCode()
        result = 31 * result + protocol.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + requestHeaders.hashCode()
        result = 31 * result + responseHeaders.hashCode()
        result = 31 * result + requestBody.hashCode()
        result = 31 * result + requestContentType.hashCode()
        result = 31 * result + requestContentLength.hashCode()
        result = 31 * result + responseBody.hashCode()
        result = 31 * result + responseContentType.hashCode()
        result = 31 * result + responseContentLength.hashCode()
        result = 31 * result + requestDate.hashCode()
        result = 31 * result + responseDate.hashCode()
        result = 31 * result + responseTlsVersion.hashCode()
        result = 31 * result + responseCipherSuite.hashCode()
        result = 31 * result + responseCode
        result = 31 * result + responseMessage.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "HttpInformation(id=$id, url='$url', host='$host', path='$path', scheme='$scheme', protocol='$protocol', method='$method', requestHeaders=$requestHeaders, responseHeaders=$responseHeaders, requestBody='$requestBody', requestContentType='$requestContentType', requestContentLength=$requestContentLength, responseBody='$responseBody', responseContentType='$responseContentType', responseContentLength=$responseContentLength, requestDate=$requestDate, responseDate=$responseDate, responseTlsVersion='$responseTlsVersion', responseCipherSuite='$responseCipherSuite', responseCode=$responseCode, responseMessage='$responseMessage', error=$error)"
    }

}