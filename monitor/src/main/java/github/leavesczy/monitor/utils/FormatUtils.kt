package github.leavesczy.monitor.utils

import android.annotation.SuppressLint
import android.text.format.Formatter
import github.leavesczy.monitor.db.MonitorHttp
import github.leavesczy.monitor.db.MonitorHttpDetail
import github.leavesczy.monitor.db.MonitorHttpHeader
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
@SuppressLint("ConstantLocale")
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

    fun formatHeaders(httpHeaders: List<MonitorHttpHeader>, withMarkup: Boolean): String {
        return buildString {
            for ((name, value) in httpHeaders) {
                if (withMarkup) {
                    append("<b>")
                }
                append(name)
                append(" : ")
                if (withMarkup) {
                    append("</b>")
                }
                append(value)
                if (withMarkup) {
                    append("<br/>")
                } else {
                    append("\n")
                }
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

    fun getShareText(monitorHttp: MonitorHttp): String {
        return buildString {
            val httpOverview = buildMonitorHttpOverview(monitorHttp = monitorHttp)
            httpOverview.forEach {
                append(it.header)
                append(" : ")
                append(it.value)
                append("\n")
            }
            append("\n")
            append("----------Request----------")
            append("\n\n")
            append(monitorHttp.getRequestHeadersString(withMarkup = false))
            append(monitorHttp.requestBodyFormat)
            append("\n\n")
            append("----------Response----------")
            append("\n\n")
            append(monitorHttp.getResponseHeadersString(withMarkup = false))
            append(monitorHttp.responseBodyFormat)
        }
    }

    fun buildMonitorHttpOverview(monitorHttp: MonitorHttp): List<MonitorHttpDetail> {
        return buildList {
            add(
                MonitorHttpDetail(
                    header = "Url",
                    value = monitorHttp.url
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Method",
                    value = monitorHttp.method
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Protocol",
                    value = monitorHttp.protocol
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Status",
                    value = monitorHttp.httpStatus.toString()
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Response",
                    value = monitorHttp.responseSummaryText
                )
            )
            add(
                MonitorHttpDetail(
                    header = "TlsVersion",
                    value = monitorHttp.responseTlsVersion
                )
            )
            add(
                MonitorHttpDetail(
                    header = "CipherSuite",
                    value = monitorHttp.responseCipherSuite
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Request Time",
                    value = monitorHttp.requestDateYMDHMSS
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Response Time",
                    value = monitorHttp.responseDateYMDHMSS
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Duration",
                    value = monitorHttp.requestDurationFormat
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Request Size",
                    value = formatBytes(monitorHttp.requestContentLength)
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Response Size",
                    value = formatBytes(monitorHttp.responseContentLength)
                )
            )
            add(
                MonitorHttpDetail(
                    header = "Total Size",
                    value = monitorHttp.totalSizeFormat
                )
            )
        }
    }

}