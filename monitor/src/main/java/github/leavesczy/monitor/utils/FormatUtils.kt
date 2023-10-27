package github.leavesczy.monitor.utils

import android.text.format.Formatter
import github.leavesczy.monitor.db.Monitor
import github.leavesczy.monitor.db.MonitorDetail
import github.leavesczy.monitor.db.MonitorHeader
import github.leavesczy.monitor.provider.ContextProvider
import github.leavesczy.monitor.provider.JsonProvider
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
 * @Date: 2020/11/7 21:15
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object FormatUtils {

    fun getDateMDHMS(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(Date(date))
    }

    fun getDateYMDHMSS(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault())
        return simpleDateFormat.format(Date(date))
    }

    fun formatBytes(bytes: Long): String {
        return Formatter.formatFileSize(ContextProvider.context, bytes)
    }

    private fun formatHeaders(headers: List<MonitorHeader>): String {
        return buildString {
            for ((name, value) in headers) {
                append(name)
                append(" : ")
                append(value)
                append("\n")
            }
        }
    }

    fun formatBody(body: String, contentType: String): String {
        return when {
            body.isBlank() -> {
                ""
            }

            contentType.contains("json", true) -> {
                JsonProvider.setPrettyPrinting(body)
            }

            contentType.contains("xml", true) -> {
                formatXml(body)
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

    fun getShareText(monitor: Monitor): String {
        return buildString {
            val overview = buildMonitorOverview(monitor = monitor)
            overview.forEach {
                append(it.header)
                append(" : ")
                append(it.value)
                append("\n")
            }
            append("\n")
            append("----------Request----------")
            append("\n\n")
            append(formatHeaders(monitor.requestHeaders))
            append(monitor.requestBodyFormat)
            append("\n\n")
            append("----------Response----------")
            append("\n\n")
            append(formatHeaders(monitor.responseHeaders))
            append(monitor.responseBodyFormat)
        }
    }

    fun buildMonitorOverview(monitor: Monitor): List<MonitorDetail> {
        return buildList {
            add(MonitorDetail(header = "Url", value = monitor.url))
            add(MonitorDetail(header = "Method", value = monitor.method))
            add(MonitorDetail(header = "Protocol", value = monitor.protocol))
            add(MonitorDetail(header = "Status", value = monitor.httpStatus.toString()))
            add(MonitorDetail(header = "Response", value = monitor.responseSummaryText))
            add(MonitorDetail(header = "TlsVersion", value = monitor.responseTlsVersion))
            add(MonitorDetail(header = "CipherSuite", value = monitor.responseCipherSuite))
            add(MonitorDetail(header = "Request Time", value = monitor.requestDateYMDHMSS))
            add(MonitorDetail(header = "Response Time", value = monitor.responseDateYMDHMSS))
            add(MonitorDetail(header = "Duration", value = monitor.requestDurationFormat))
            add(
                MonitorDetail(
                    header = "Request Size",
                    value = formatBytes(monitor.requestContentLength)
                )
            )
            add(
                MonitorDetail(
                    header = "Response Size",
                    value = formatBytes(monitor.responseContentLength)
                )
            )
            add(MonitorDetail(header = "Total Size", value = monitor.totalSizeFormat))
        }
    }

}