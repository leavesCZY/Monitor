package github.leavesczy.monitor.internal.capture

import github.leavesczy.monitor.internal.core.MonitorConstants
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

internal object MonitorHttpBodyCapture {

    data class RequestCapture(
        val bodyText: String?,
        val contentLength: Long,
        val contentType: String,
        val request: Request
    )

    data class ResponseCapture(
        val bodyText: String,
        val contentLength: Long
    )

    fun captureRequest(request: Request): RequestCapture {
        return runCatching {
            captureRequestInternal(request = request)
        }.getOrElse { error ->
            error.printStackTrace()
            emptyRequestCapture(request = request)
        }
    }

    fun captureResponse(response: Response): ResponseCapture {
        return runCatching {
            captureResponseInternal(response = response)
        }.getOrElse { error ->
            error.printStackTrace()
            emptyResponseCapture(response = response)
        }
    }

    private fun captureRequestInternal(request: Request): RequestCapture {
        val body = request.body ?: return emptyRequestCapture(request = request)
        val contentLength = body.contentLength()
        val contentType = body.contentType()?.toString() ?: ""
        if (!isRequestBodyReadable(
                body = body,
                headers = request.headers,
                contentLength = contentLength
            )
        ) {
            return RequestCapture(
                bodyText = "",
                contentLength = contentLength,
                contentType = contentType,
                request = request
            )
        }
        val buffer = Buffer()
        body.writeTo(sink = buffer)
        val bytes = buffer.readByteArray()
        val resolvedContentLength = resolveContentLength(
            headerContentLength = contentLength,
            actualBytes = bytes.size.toLong()
        )
        val replayRequest = rebuildRequest(
            request = request,
            body = body,
            bytes = bytes
        )
        if (bytes.size > MonitorConstants.MAX_BODY_BYTES) {
            return RequestCapture(
                bodyText = "",
                contentLength = resolvedContentLength,
                contentType = contentType,
                request = replayRequest
            )
        }
        val charset = body.contentType()?.charset() ?: StandardCharsets.UTF_8
        return RequestCapture(
            bodyText = readUtf8Text(bytes = bytes, charset = charset),
            contentLength = resolvedContentLength,
            contentType = contentType,
            request = replayRequest
        )
    }

    private fun captureResponseInternal(response: Response): ResponseCapture {
        val body = response.body
        val headerContentLength = body.contentLength()
        if (!response.promisesBody() || response.headers.hasUnknownContentEncoding()) {
            return ResponseCapture(
                bodyText = "",
                contentLength = headerContentLength.coerceAtLeast(minimumValue = 0L)
            )
        }
        val peekedBody = response.peekBody(byteCount = MonitorConstants.MAX_BODY_BYTES)
        val rawBuffer = Buffer()
        peekedBody.source().use { source ->
            source.readAll(sink = rawBuffer)
        }
        val decodedBuffer = if (response.headers.isGzipEncoded()) {
            decompressGzip(buffer = rawBuffer)
        } else {
            rawBuffer
        }
        val bodyText = readUtf8Text(
            bytes = decodedBuffer.readByteArray(),
            charset = body.contentType()?.charset() ?: StandardCharsets.UTF_8
        )
        return ResponseCapture(
            bodyText = bodyText,
            contentLength = resolveContentLength(
                headerContentLength = headerContentLength,
                actualBytes = peekedBody.contentLength().coerceAtLeast(minimumValue = 0L)
            )
        )
    }

    private fun emptyRequestCapture(request: Request): RequestCapture {
        val body = request.body
        return RequestCapture(
            bodyText = if (body == null) null else "",
            contentLength = body?.contentLength() ?: 0L,
            contentType = body?.contentType()?.toString() ?: "",
            request = request
        )
    }

    private fun emptyResponseCapture(response: Response): ResponseCapture {
        return ResponseCapture(
            bodyText = "",
            contentLength = response.body.contentLength().coerceAtLeast(minimumValue = 0L)
        )
    }

    private fun rebuildRequest(
        request: Request,
        body: RequestBody,
        bytes: ByteArray
    ): Request {
        return request.newBuilder()
            .method(
                method = request.method,
                body = bytes.toRequestBody(contentType = body.contentType())
            )
            .build()
    }

    private fun resolveContentLength(headerContentLength: Long, actualBytes: Long): Long {
        return when {
            headerContentLength >= 0L -> headerContentLength
            actualBytes > 0L -> actualBytes
            else -> 0L
        }
    }

    private fun isRequestBodyReadable(
        body: RequestBody,
        headers: Headers,
        contentLength: Long
    ): Boolean {
        return !headers.hasUnknownContentEncoding() &&
                !body.isDuplex() &&
                !body.isOneShot() &&
                contentLength in 0L..MonitorConstants.MAX_BODY_BYTES
    }

    private fun readUtf8Text(bytes: ByteArray, charset: Charset): String {
        if (bytes.isEmpty() || !Utf8Detector.isProbablyUtf8(bytes = bytes)) {
            return ""
        }
        return Buffer().write(bytes).readString(charset = charset)
    }

    private fun decompressGzip(buffer: Buffer): Buffer {
        val decompressedBuffer = Buffer()
        GzipSource(source = buffer.clone()).use { gzipSource ->
            val maxBytes = MonitorConstants.MAX_BODY_BYTES
            while (decompressedBuffer.size < maxBytes) {
                val bytesRead = gzipSource.read(
                    sink = decompressedBuffer,
                    byteCount = maxBytes - decompressedBuffer.size
                )
                if (bytesRead == -1L) {
                    break
                }
            }
        }
        return decompressedBuffer
    }

}

private object Utf8Detector {

    fun isProbablyUtf8(bytes: ByteArray): Boolean {
        return try {
            val prefix = Buffer().write(bytes)
            val sample = Buffer()
            val byteCount = prefix.size.coerceAtMost(maximumValue = 64)
            prefix.copyTo(out = sample, offset = 0, byteCount = byteCount)
            repeat(times = 16) {
                if (sample.exhausted()) {
                    return@repeat
                }
                val codePoint = sample.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (_: EOFException) {
            false
        }
    }

}