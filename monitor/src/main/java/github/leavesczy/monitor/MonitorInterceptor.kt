package github.leavesczy.monitor

import github.leavesczy.monitor.internal.ContextProvider
import github.leavesczy.monitor.internal.MonitorNotificationHandler
import github.leavesczy.monitor.internal.db.Monitor
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.db.MonitorPair
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 22:07
 * @Desc:
 */
class MonitorInterceptor : Interceptor {

    @Volatile
    private var notificationHandleInitialized = false

    override fun intercept(chain: Interceptor.Chain): Response {
        initNotificationHandlerIfNeed()
        val request = chain.request()
        var monitor = buildMonitor(request = request)
        monitor = insert(monitor = monitor)
        val response = try {
            chain.proceed(request = request)
        } catch (throwable: Throwable) {
            update(monitor = monitor.copy(error = throwable.toString()))
            throw throwable
        }
        monitor = processResponse(
            response = response,
            monitor = monitor
        )
        update(monitor = monitor)
        return response
    }

    private fun initNotificationHandlerIfNeed() {
        if (!notificationHandleInitialized) {
            synchronized(this) {
                if (!notificationHandleInitialized) {
                    notificationHandleInitialized = true
                    MonitorNotificationHandler.init(context = ContextProvider.context)
                }
            }
        }
    }

    private fun buildMonitor(request: Request): Monitor {
        val requestDate = System.currentTimeMillis()
        val requestBody = request.body
        val url = request.url
        val scheme = url.scheme
        val host = url.host
        val path = url.encodedPath
        val query = url.encodedQuery ?: ""
        val method = request.method
        val requestHeaders = request.headers.map {
            MonitorPair(name = it.first, value = it.second)
        }
        val mRequestBody = if (requestBody == null) {
            null
        } else if (request.headers.bodyHasUnknownEncoding() || requestBody.isDuplex() || requestBody.isOneShot()) {
            ""
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            if (buffer.isProbablyUtf8()) {
                val read = buffer.readString(charset)
                read.ifBlank {
                    null
                }
            } else {
                ""
            }
        }
        val requestContentLength = requestBody?.contentLength() ?: 0
        val requestContentType = requestBody?.contentType()?.toString() ?: ""
        return Monitor(
            id = 0L,
            url = url.toString(),
            scheme = scheme,
            host = host,
            path = path,
            query = query,
            requestDate = requestDate,
            method = method,
            requestHeaders = requestHeaders,
            requestContentLength = requestContentLength,
            requestContentType = requestContentType,
            requestBody = mRequestBody,
            protocol = "",
            responseHeaders = emptyList(),
            responseBody = "",
            responseContentType = "",
            responseContentLength = 0L,
            responseDate = 0L,
            responseTlsVersion = "",
            responseCipherSuite = "",
            responseMessage = "",
            error = null
        )
    }

    private fun processResponse(
        response: Response,
        monitor: Monitor
    ): Monitor {
        val requestHeaders = response.request.headers.map {
            MonitorPair(name = it.first, value = it.second)
        }
        val responseHeaders = response.headers.map {
            MonitorPair(name = it.first, value = it.second)
        }
        val responseBody = response.body
        val responseContentType = responseBody?.contentType()?.toString() ?: ""
        var responseContentLength = responseBody?.contentLength() ?: 0
        val mResponseBody = if (responseBody == null) {
            null
        } else if (!response.promisesBody() || response.headers.bodyHasUnknownEncoding()) {
            ""
        } else {
            val buffer = response.getNativeSource()
            responseContentLength = buffer.size
            if (buffer.isProbablyUtf8()) {
                if (responseContentLength != 0L) {
                    val charset = responseBody.contentType()?.charset(Charsets.UTF_8)
                        ?: Charsets.UTF_8
                    val read = buffer.clone().readString(charset)
                    read.ifBlank {
                        null
                    }
                } else {
                    ""
                }
            } else {
                ""
            }
        }
        return monitor.copy(
            requestDate = response.sentRequestAtMillis,
            responseDate = response.receivedResponseAtMillis,
            protocol = response.protocol.toString(),
            responseCode = response.code,
            responseMessage = response.message,
            responseTlsVersion = response.handshake?.tlsVersion?.javaName ?: "",
            responseCipherSuite = response.handshake?.cipherSuite?.javaName ?: "",
            requestHeaders = requestHeaders,
            responseHeaders = responseHeaders,
            responseContentType = responseContentType,
            responseContentLength = responseContentLength,
            responseBody = mResponseBody
        )
    }

    private fun insert(monitor: Monitor): Monitor {
        val id = MonitorDatabase.instance.monitorDao.insertMonitor(monitor = monitor)
        return monitor.copy(id = id)
    }

    private fun update(monitor: Monitor) {
        MonitorDatabase.instance.monitorDao.updateMonitor(monitor = monitor)
    }

}

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = buffer.size.coerceAtMost(64)
        buffer.copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false
    }
}

internal fun Headers.bodyGzipped(): Boolean {
    return this["Content-Encoding"].equals(other = "gzip", ignoreCase = true)
}

internal fun Headers.bodyHasUnknownEncoding(): Boolean {
    val contentEncoding = this["Content-Encoding"] ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
}

internal fun Response.getNativeSource(): Buffer {
    val source = body!!.source()
    source.request(Long.MAX_VALUE)
    var buffer = source.buffer
    if (headers.bodyGzipped()) {
        GzipSource(source = buffer.clone()).use { responseBody ->
            buffer = Buffer()
            buffer.writeAll(source = responseBody)
        }
    }
    return buffer
}