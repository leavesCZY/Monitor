package github.leavesczy.monitor.internal.capture

import github.leavesczy.monitor.internal.db.MonitorRecord
import okhttp3.Request
import okhttp3.Response

internal object MonitorRecordFactory {

    fun createPending(
        request: Request,
        requestCapture: MonitorHttpBodyCapture.RequestCapture
    ): MonitorRecord {
        val url = request.url
        return MonitorRecord(
            id = 0L,
            url = url.toString(),
            scheme = url.scheme,
            host = url.host,
            path = url.encodedPath,
            query = url.query ?: "",
            requestTime = System.currentTimeMillis(),
            method = request.method,
            requestHeaders = request.headers.toMonitorHeaders(),
            requestContentLength = requestCapture.contentLength,
            requestContentType = requestCapture.contentType,
            requestBody = requestCapture.bodyText,
            protocol = "",
            responseHeaders = emptyList(),
            responseBody = "",
            responseContentType = "",
            responseContentLength = 0L,
            responseTime = 0L,
            responseTlsVersion = "",
            responseCipherSuite = "",
            responseMessage = "",
            error = null
        )
    }

    fun complete(
        record: MonitorRecord,
        response: Response,
        responseCapture: MonitorHttpBodyCapture.ResponseCapture
    ): MonitorRecord {
        return record.copy(
            requestTime = response.sentRequestAtMillis,
            responseTime = response.receivedResponseAtMillis,
            protocol = response.protocol.toString(),
            responseCode = response.code,
            responseMessage = response.message,
            responseTlsVersion = response.handshake?.tlsVersion?.javaName ?: "",
            responseCipherSuite = response.handshake?.cipherSuite?.javaName ?: "",
            requestHeaders = response.request.headers.toMonitorHeaders(),
            responseHeaders = response.headers.toMonitorHeaders(),
            responseContentType = response.body.contentType()?.toString() ?: "",
            responseContentLength = responseCapture.contentLength,
            responseBody = responseCapture.bodyText
        )
    }

    fun fail(record: MonitorRecord, error: Throwable): MonitorRecord {
        val errorMessage = buildString {
            append(error::class.java.simpleName)
            val message = error.message
            if (!message.isNullOrBlank()) {
                append(": ")
                append(message)
            }
        }
        return record.copy(
            error = errorMessage,
            responseTime = System.currentTimeMillis()
        )
    }

}