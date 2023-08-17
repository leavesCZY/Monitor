package github.leavesczy.monitor.utils

import github.leavesczy.monitor.db.MonitorHttp
import github.leavesczy.monitor.db.MonitorHttpHeader
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
import kotlin.math.ln
import kotlin.math.pow

/**
 * @Author: leavesCZY
 * @Date: 2020/11/7 21:15
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object FormatUtils {

    private val TIME_SHORT = SimpleDateFormat("HH:mm:ss SSS", Locale.CHINA)

    private val TIME_LONG = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA)

    fun getDateFormatShort(date: Long): String {
        return TIME_SHORT.format(Date(date))
    }

    fun getDateFormatLong(date: Long): String {
        return TIME_LONG.format(Date(date))
    }

    fun formatBytes(bytes: Long): String {
        return formatByteCount(bytes, true)
    }

    @Suppress("SameParameterValue")
    private fun formatByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) {
            return "$bytes B"
        }
        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = (if (si) {
            "kMGTPE"
        } else {
            "KMGTPE"
        })[exp - 1] + if (si) {
            ""
        } else {
            "i"
        }
        return String.format(
            Locale.US,
            "%.1f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()),
            pre
        )
    }

    fun formatHeaders(httpHeaders: List<MonitorHttpHeader>, withMarkup: Boolean): String {
        val out = StringBuilder()
        for ((name, value) in httpHeaders) {
            out.append(if (withMarkup) "<b>" else "")
                .append(name)
                .append(": ")
                .append(if (withMarkup) "</b>" else "")
                .append(value)
                .append(if (withMarkup) "<br />" else "\n")
        }
        return out.toString()
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

    fun getShareText(monitorHttp: MonitorHttp): String {
        var text = ""
        text += "Url: " + monitorHttp.url + "\n"
        text += "Host: " + monitorHttp.host + "\n"
        text += "Path: " + monitorHttp.path + "\n"
        text += "Scheme: " + monitorHttp.scheme + "\n"
        text += "\n"
        text += "Method: " + monitorHttp.method + "\n"
        text += "Protocol: " + monitorHttp.protocol + "\n"
        text += "Status: " + monitorHttp.httpStatus.toString() + "\n"
        text += "Response: " + monitorHttp.responseSummaryText + "\n"
        text += "SSL: " + monitorHttp.isSsl + "\n"
        text += "TlsVersion: " + monitorHttp.responseTlsVersion + "\n"
        text += "CipherSuite: " + monitorHttp.responseCipherSuite + "\n"
        text += "\n"
        text += "Request Time: " + getDateFormatLong(monitorHttp.requestDate) + "\n"
        text += "Response Time: " + getDateFormatLong(monitorHttp.responseDate) + "\n"
        text += "Duration: " + monitorHttp.durationFormat + "\n"
        text += "\n"
        text += "Request Size: " + formatBytes(monitorHttp.requestContentLength) + "\n"
        text += "Response Size: " + formatBytes(monitorHttp.responseContentLength) + "\n"
        text += "Total Size: " + monitorHttp.totalSizeFormat + "\n"
        text += "\n"
        text += "---------- Request  ----------\n"
        text += monitorHttp.getRequestHeadersString(false) + "\n"
        text += monitorHttp.requestBodyFormat + "\n"
        text += "\n"
        text += "---------- Response  ----------\n"
        text += monitorHttp.getResponseHeadersString(false) + "\n"
        text += monitorHttp.responseBodyFormat + "\n"
        return text
    }

}