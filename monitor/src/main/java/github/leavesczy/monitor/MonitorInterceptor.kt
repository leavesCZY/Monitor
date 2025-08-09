package github.leavesczy.monitor

import github.leavesczy.monitor.internal.ContextProvider
import github.leavesczy.monitor.internal.MonitorNotification
import github.leavesczy.monitor.internal.db.Monitor
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.db.MonitorPair
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
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

    private companion object {

        @Volatile
        var isMonitorNotificationInitializeCalled = false

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        initNotification()
        val request = chain.request()
        var monitor = insert(monitor = buildMonitor(request = request))
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

    private fun initNotification() {
        if (!isMonitorNotificationInitializeCalled) {
            synchronized(lock = Unit) {
                if (!isMonitorNotificationInitializeCalled) {
                    MonitorNotification.init(context = ContextProvider.context)
                    isMonitorNotificationInitializeCalled = true
                }
            }
        }
    }

    private fun buildMonitor(request: Request): Monitor {
        val requestTime = System.currentTimeMillis()
        val url = request.url
        val scheme = url.scheme
        val host = url.host
        val path = url.encodedPath
        val query = url.query ?: ""
        val method = request.method
        val headers = request.headers
        val body = request.body
        val requestHeaders = headers.map {
            MonitorPair(name = it.first, value = it.second)
        }
        val requestBody: String?
        val requestContentLength: Long
        val requestContentType: String
        if (body == null) {
            requestBody = null
            requestContentLength = 0L
            requestContentType = ""
        } else {
            requestBody = if (headers.bodyHasUnknownEncoding() ||
                body.isDuplex() ||
                body.isOneShot()
            ) {
                ""
            } else {
                val buffer = Buffer()
                body.writeTo(sink = buffer)
                if (buffer.isProbablyUtf8()) {
                    val charset = body.contentType()?.charset() ?: Charsets.UTF_8
                    buffer.readString(charset = charset)
                } else {
                    ""
                }
            }
            requestContentLength = body.contentLength()
            requestContentType = body.contentType().toString()
        }
        return Monitor(
            id = 0L,
            url = url.toString(),
            scheme = scheme,
            host = host,
            path = path,
            query = query,
            requestTime = requestTime,
            method = method,
            requestHeaders = requestHeaders,
            requestContentLength = requestContentLength,
            requestContentType = requestContentType,
            requestBody = requestBody,
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
        val body = response.body
        val responseContentType = body.contentType()?.toString() ?: ""
        var responseContentLength = body.contentLength()
        val responseBody = if (!response.promisesBody() ||
            response.headers.bodyHasUnknownEncoding()
        ) {
            ""
        } else {
            val buffer = getNativeSource(
                responseBody = body,
                headers = response.headers
            )
            responseContentLength = buffer.size
            if (buffer.isProbablyUtf8() && responseContentLength != 0L) {
                val charset = body.contentType()?.charset() ?: Charsets.UTF_8
                buffer.clone().readString(charset = charset)
            } else {
                ""
            }
        }
        return monitor.copy(
            requestTime = response.sentRequestAtMillis,
            responseTime = response.receivedResponseAtMillis,
            protocol = response.protocol.toString(),
            responseCode = response.code,
            responseMessage = response.message,
            responseTlsVersion = response.handshake?.tlsVersion?.javaName ?: "",
            responseCipherSuite = response.handshake?.cipherSuite?.javaName ?: "",
            requestHeaders = requestHeaders,
            responseHeaders = responseHeaders,
            responseContentType = responseContentType,
            responseContentLength = responseContentLength,
            responseBody = responseBody
        )
    }

    private fun insert(monitor: Monitor): Monitor {
        val id = MonitorDatabase.instance.monitorDao.insertMonitor(monitor = monitor)
        return monitor.copy(id = id)
    }

    private fun update(monitor: Monitor) {
        MonitorDatabase.instance.monitorDao.updateMonitor(monitor = monitor)
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(maximumValue = 64)
            copyTo(out = prefix, offset = 0, byteCount = byteCount)
            repeat(times = 16) {
                if (prefix.exhausted()) {
                    return@repeat
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (_: EOFException) {
            false
        }
    }

    private fun Headers.bodyGzipped(): Boolean {
        return this["Content-Encoding"].equals(other = "gzip", ignoreCase = true)
    }

    private fun Headers.bodyHasUnknownEncoding(): Boolean {
        val contentEncoding = this["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun getNativeSource(
        responseBody: ResponseBody,
        headers: Headers
    ): Buffer {
        val source = responseBody.source()
        source.request(byteCount = Long.MAX_VALUE)
        var buffer = source.buffer
        if (headers.bodyGzipped()) {
            GzipSource(source = buffer.clone()).use { responseBody ->
                buffer = Buffer()
                buffer.writeAll(source = responseBody)
            }
        }
        return buffer
    }

}