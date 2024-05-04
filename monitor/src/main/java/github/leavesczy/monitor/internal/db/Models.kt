package github.leavesczy.monitor.internal.db

import android.text.format.Formatter
import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.ContextProvider
import github.leavesczy.monitor.internal.JsonFormat
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 */
@Stable
internal data class MonitorPair(
    val name: String,
    val value: String
)

@Stable
internal enum class MonitorStatus {
    Requesting,
    Complete,
    Failed
}

@Stable
@Entity(tableName = MonitorDatabase.MONITOR_TABLE_NAME)
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
    val requestHeaders: List<MonitorPair>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String?,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestDate")
    val requestDate: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorPair>,
    @ColumnInfo(name = "responseBody")
    val responseBody: String?,
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

    val httpStatus: MonitorStatus
        get() = when {
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

    val notificationText: String
        get() = when (httpStatus) {
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

    val pathWithQuery: String
        get() = if (query.isBlank()) {
            path
        } else {
            String.format("%s?%s", path, query)
        }

    val requestBodyFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        formatRequestBody()
    }

    val responseBodyFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        formatResponseBody()
    }

    val responseCodeFormat: String
        get() = when (httpStatus) {
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

    val requestDateMDHMS by lazy(mode = LazyThreadSafetyMode.NONE) {
        getDateMDHMS(date = requestDate)
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
                formatBytes(bytes = requestContentLength + responseContentLength)
            }

            MonitorStatus.Failed -> {
                ""
            }
        }
    }

}

internal fun Monitor.buildOverview(): List<MonitorPair> {
    val responseSummaryText = when (httpStatus) {
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
    return buildList {
        add(MonitorPair(name = "Url", value = url))
        add(MonitorPair(name = "Method", value = method))
        add(MonitorPair(name = "Protocol", value = protocol))
        add(MonitorPair(name = "State", value = httpStatus.toString()))
        add(MonitorPair(name = "Response", value = responseSummaryText))
        add(MonitorPair(name = "TlsVersion", value = responseTlsVersion))
        add(MonitorPair(name = "CipherSuite", value = responseCipherSuite))
        add(MonitorPair(name = "Request Time", value = getDateYMDHMSS(date = requestDate)))
        add(MonitorPair(name = "Response Time", value = getDateYMDHMSS(date = responseDate)))
        add(MonitorPair(name = "Duration", value = requestDurationFormat))
        add(
            MonitorPair(
                name = "Request Size",
                value = formatBytes(bytes = requestContentLength)
            )
        )
        add(
            MonitorPair(
                name = "Response Size",
                value = formatBytes(bytes = responseContentLength)
            )
        )
        add(MonitorPair(name = "Total Size", value = totalSizeFormat))
    }
}

private fun formatBytes(bytes: Long): String {
    return Formatter.formatFileSize(ContextProvider.context, bytes)
}

private fun getDateMDHMS(date: Long): String {
    val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
    return simpleDateFormat.format(Date(date))
}

private fun getDateYMDHMSS(date: Long): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault())
    return simpleDateFormat.format(Date(date))
}

private fun Monitor.formatRequestBody(): String {
    return formatBody(requestBody, requestContentType)
}

private fun Monitor.formatResponseBody(): String {
    return formatBody(responseBody, responseContentType)
}

private fun formatBody(body: String?, contentType: String): String {
    return when {
        body == null -> {
            ""
        }

        body.isBlank() -> {
            ContextProvider.context.getString(R.string.monitor_encoded_body_omitted)
        }

        contentType.contains("json", true) -> {
            JsonFormat.toPrettyJson(json = body)
        }

        contentType.contains("xml", true) -> {
            formatXml(xml = body)
        }

        else -> {
            body
        }
    }
}

private fun formatXml(xml: String): String {
    return try {
        val documentFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        // This flag is required for security reasons
        documentFactory.isExpandEntityReferences = false
        val documentBuilder: DocumentBuilder = documentFactory.newDocumentBuilder()
        val inputSource =
            InputSource(ByteArrayInputStream(xml.toByteArray(Charset.defaultCharset())))
        val document: Document = documentBuilder.parse(inputSource)
        val domSource = DOMSource(document)
        val writer = StringWriter()
        val result = StreamResult(writer)
        TransformerFactory.newInstance().apply {
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        }.newTransformer().apply {
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            setOutputProperty(OutputKeys.INDENT, "yes")
            transform(domSource, result)
        }
        writer.toString()
    } catch (e: SAXParseException) {
        xml
    } catch (io: IOException) {
        xml
    } catch (t: TransformerException) {
        xml
    }
}

internal fun Monitor.buildShareText(): String {
    return buildString {
        kotlin.run {
            append(buildOverview().format())
        }
        append("\n\n")
        kotlin.run {
            append("----------Request----------")
            append("\n\n")
            append(requestHeaders.format())
            if (requestBodyFormat.isNotBlank()) {
                append("\n\n")
                append(requestBodyFormat)
            }
        }
        append("\n\n")
        kotlin.run {
            append("----------Response----------")
            append("\n\n")
            append(responseHeaders.format())
            append("\n\n")
            append(responseBodyFormat)
        }
    }
}

private fun List<MonitorPair>.format(): String {
    return buildString {
        this@format.forEachIndexed { index, pair ->
            append(pair.name)
            append(" : ")
            append(pair.value)
            if (index != this@format.size - 1) {
                append("\n")
            }
        }
    }
}