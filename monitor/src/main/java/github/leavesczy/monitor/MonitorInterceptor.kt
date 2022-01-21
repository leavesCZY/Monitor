package github.leavesczy.monitor

import android.app.Application
import android.net.Uri
import github.leavesczy.monitor.db.HttpInformation
import github.leavesczy.monitor.db.MonitorHttpInformationDatabase
import github.leavesczy.monitor.holder.ContextHolder
import github.leavesczy.monitor.holder.NotificationHolder
import github.leavesczy.monitor.utils.ResponseUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.IOException

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:26
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MonitorInterceptor(context: Application) : Interceptor {

    companion object {

        private val CHARSET_UTF8 = Charsets.UTF_8

        private const val UnknownEncoding = "(encoded body omitted)"

    }

    init {
        ContextHolder.init(context = context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val httpInformation = HttpInformation()

        processRequest(request, httpInformation)

        httpInformation.id = insert(httpInformation)

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Throwable) {
            httpInformation.error = e.toString()
            throw e
        } finally {
            try {
                update(httpInformation)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        try {
            processResponse(response, httpInformation)
            update(httpInformation)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return response
    }

    private fun processRequest(request: Request, httpInformation: HttpInformation) {
        val requestBody = request.body

        val url = request.url.toString()
        val uri = Uri.parse(url)

        httpInformation.url = url
        httpInformation.host = uri.host ?: ""
        httpInformation.path =
            (uri.path ?: "") + if (uri.query.isNullOrBlank()) "" else ("?" + uri.query)
        httpInformation.scheme = uri.scheme ?: ""

        httpInformation.requestDate = System.currentTimeMillis()
        httpInformation.method = request.method
        httpInformation.setRequestHttpHeaders(request.headers)

        httpInformation.requestContentLength = requestBody?.contentLength() ?: 0
        httpInformation.requestContentType = requestBody?.contentType()?.toString() ?: ""

        if (requestBody != null && ResponseUtils.bodyHasSupportedEncoding(request.headers)) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            if (ResponseUtils.isProbablyUtf8(buffer)) {
                val charset = requestBody.contentType()?.charset(CHARSET_UTF8) ?: CHARSET_UTF8
                val content = buffer.readString(charset)
                httpInformation.requestBody = content
            }
        }
    }

    private fun processResponse(response: Response, httpInformation: HttpInformation) {
        httpInformation.requestDate = response.sentRequestAtMillis
        httpInformation.responseDate = response.receivedResponseAtMillis
        httpInformation.protocol = response.protocol.toString()
        httpInformation.responseCode = response.code
        httpInformation.responseMessage = response.message
        httpInformation.setRequestHttpHeaders(response.request.headers)
        httpInformation.setResponseHttpHeaders(response.headers)

        httpInformation.responseTlsVersion = response.handshake?.tlsVersion?.javaName ?: ""
        httpInformation.responseCipherSuite = response.handshake?.cipherSuite?.javaName ?: ""

        val responseBody = response.body

        if (responseBody != null) {
            httpInformation.responseContentType = responseBody.contentType()?.toString() ?: ""
            httpInformation.responseContentLength = responseBody.contentLength()
            if (response.promisesBody()) {
                val encodingIsSupported =
                    ResponseUtils.bodyHasSupportedEncoding(response.headers)
                if (encodingIsSupported) {
                    val buffer = ResponseUtils.getNativeSource(response)
                    httpInformation.responseContentLength = buffer.size
                    if (ResponseUtils.isProbablyUtf8(buffer)) {
                        if (responseBody.contentLength() != 0L) {
                            val charset =
                                responseBody.contentType()?.charset(CHARSET_UTF8)
                                    ?: CHARSET_UTF8
                            httpInformation.responseBody = buffer.clone().readString(charset)
                            return
                        }
                    }
                }
                httpInformation.responseBody = UnknownEncoding
            }
        }
    }

    private fun insert(httpInformation: HttpInformation): Long {
        showNotification(httpInformation)
        return MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.insert(httpInformation)
    }

    private fun update(httpInformation: HttpInformation) {
        showNotification(httpInformation)
        MonitorHttpInformationDatabase.INSTANCE.httpInformationDao.update(httpInformation)
    }

    private fun showNotification(httpInformation: HttpInformation) {
        NotificationHolder.show(httpInformation)
    }

}