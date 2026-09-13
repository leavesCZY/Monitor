package github.leavesczy.monitor.internal.capture

import github.leavesczy.monitor.internal.db.MonitorPayload
import github.leavesczy.monitor.internal.db.MonitorRecord
import okhttp3.Request
import okhttp3.Response

internal data class MonitorEntry(
    val record: MonitorRecord,
    val payload: MonitorPayload
)

internal object MonitorRecordFactory {

    fun createPending(
        request: Request,
        requestCapture: MonitorHttpBodyCapture.RequestCapture
    ): MonitorEntry {
        val url = request.url
        return MonitorEntry(
            record = MonitorRecord(
                id = 0L,
                url = url.toString(),
                scheme = url.scheme,
                host = url.host,
                path = url.encodedPath,
                query = url.query ?: "",
                requestTime = System.currentTimeMillis(),
                method = request.method,
                requestContentLength = requestCapture.contentLength,
                requestContentType = requestCapture.contentType,
                protocol = "",
                responseContentType = "",
                responseContentLength = 0L,
                responseTime = 0L,
                responseTlsVersion = "",
                responseCipherSuite = "",
                responseMessage = "",
                error = null
            ),
            payload = MonitorPayload(
                recordId = 0L,
                requestHeaders = request.headers.toMonitorHeaders(),
                requestBody = requestCapture.bodyText,
                responseHeaders = emptyList(),
                responseBody = ""
            )
        )
    }

    fun complete(
        entry: MonitorEntry,
        response: Response,
        responseCapture: MonitorHttpBodyCapture.ResponseCapture
    ): MonitorEntry {
        return entry.copy(
            record = entry.record.copy(
                requestTime = response.sentRequestAtMillis,
                responseTime = response.receivedResponseAtMillis,
                protocol = response.protocol.toString(),
                responseCode = response.code,
                responseMessage = response.message,
                responseTlsVersion = response.handshake?.tlsVersion?.javaName ?: "",
                responseCipherSuite = response.handshake?.cipherSuite?.javaName ?: "",
                responseContentType = response.body.contentType()?.toString() ?: "",
                responseContentLength = responseCapture.contentLength
            ),
            payload = entry.payload.copy(
                requestHeaders = response.request.headers.toMonitorHeaders(),
                responseHeaders = response.headers.toMonitorHeaders(),
                responseBody = responseCapture.bodyText
            )
        )
    }

    fun fail(entry: MonitorEntry, error: Throwable): MonitorEntry {
        val errorMessage = buildString {
            append(error::class.java.simpleName)
            val message = error.message
            if (!message.isNullOrBlank()) {
                append(": ")
                append(message)
            }
        }
        return entry.copy(
            record = entry.record.copy(
                error = errorMessage,
                responseTime = System.currentTimeMillis()
            )
        )
    }

}