package github.leavesczy.monitor.utils

import github.leavesczy.monitor.db.HttpHeader
import github.leavesczy.monitor.db.HttpInformation
import github.leavesczy.monitor.holder.SerializableHolder
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
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

    private fun formatData(date: Date?, format: SimpleDateFormat): String {
        return if (date == null) {
            ""
        } else format.format(date)
    }

    fun getDateFormatShort(date: Long?): String {
        if (date == null) {
            return ""
        }
        return formatData(Date(date), TIME_SHORT)
    }

    fun getDateFormatLong(date: Long?): String {
        if (date == null) {
            return ""
        }
        return formatData(Date(date), TIME_LONG)
    }

    fun formatBytes(bytes: Long): String {
        return formatByteCount(bytes, true)
    }

    fun formatByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) {
            return "$bytes B"
        }
        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format(
            Locale.US,
            "%.1f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()),
            pre
        )
    }

    fun formatHeaders(httpHeaders: List<HttpHeader>?, withMarkup: Boolean): String {
        val out = StringBuilder()
        if (httpHeaders != null) {
            for ((name, value) in httpHeaders) {
                out.append(if (withMarkup) "<b>" else "")
                    .append(name)
                    .append(": ")
                    .append(if (withMarkup) "</b>" else "")
                    .append(value)
                    .append(if (withMarkup) "<br />" else "\n")
            }
        }
        return out.toString()
    }

    fun formatBody(body: String, contentType: String?): String {
        return when {
            body.isBlank() -> {
                ""
            }
            contentType?.contains("json", true) == true -> {
                SerializableHolder.setPrettyPrinting(body)
            }
            contentType?.contains("xml", true) == true -> {
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

    fun getShareText(httpInformation: HttpInformation): String {
        var text = ""
        text += "Url: " + httpInformation.url + "\n"
        text += "Host: " + httpInformation.host + "\n"
        text += "Path: " + httpInformation.path + "\n"
        text += "Scheme: " + httpInformation.scheme + "\n"
        text += "\n"
        text += "Method: " + httpInformation.method + "\n"
        text += "Protocol: " + httpInformation.protocol + "\n"
        text += "Status: " + httpInformation.status.toString() + "\n"
        text += "Response: " + httpInformation.responseSummaryText + "\n"
        text += "SSL: " + httpInformation.isSsl + "\n"
        text += "TlsVersion: " + httpInformation.responseTlsVersion + "\n"
        text += "CipherSuite: " + httpInformation.responseCipherSuite + "\n"
        text += "\n"
        text += "Request Time: " + getDateFormatLong(httpInformation.requestDate) + "\n"
        text += "Response Time: " + getDateFormatLong(httpInformation.responseDate) + "\n"
        text += "Duration: " + httpInformation.durationFormat + "\n"
        text += "\n"
        text += "Request Size: " + formatBytes(httpInformation.requestContentLength) + "\n"
        text += "Response Size: " + formatBytes(httpInformation.responseContentLength) + "\n"
        text += "Total Size: " + httpInformation.totalSizeFormat + "\n"
        text += "\n"
        text += "---------- Request  ----------\n"
        text += httpInformation.getRequestHeadersString(false) + "\n"
        text += httpInformation.requestBodyFormat + "\n"
        text += "\n"
        text += "---------- Response  ----------\n"
        text += httpInformation.getResponseHeadersString(false) + "\n"
        text += httpInformation.responseBodyFormat + "\n"
        return text
    }

}