package github.leavesc.monitor.utils

import okhttp3.Headers
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.EOFException

/**
 * @Author: leavesC
 * @Date: 2020/11/15 10:29
 * @Desc:
 */
internal object ResponseUtils {

    private const val CONTENT_ENCODING = "Content-Encoding"

    fun isProbablyUtf8(buffer: Buffer): Boolean {
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
            return false // Truncated UTF-8 sequence.
        }
    }

    fun bodyHasSupportedEncoding(headers: Headers): Boolean {
        val contentEncoding = headers[CONTENT_ENCODING]
        return contentEncoding.isNullOrBlank() ||
                contentEncoding.equals("identity", ignoreCase = true) ||
                contentEncoding.equals("gzip", ignoreCase = true)
    }

    fun getNativeSource(response: Response): Buffer {
        val source = response.body!!.source()
        source.request(Long.MAX_VALUE)
        var buffer = source.buffer
        if (bodyGzipped(response.headers)) {
            GzipSource(buffer.clone()).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }
        return buffer
    }

    private fun bodyGzipped(headers: Headers): Boolean {
        return "gzip".equals(headers[CONTENT_ENCODING], ignoreCase = true)
    }

}