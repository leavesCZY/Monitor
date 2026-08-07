package github.leavesczy.monitor.internal.format

import android.text.format.Formatter
import androidx.core.content.ContextCompat
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.core.MonitorConstants
import github.leavesczy.monitor.internal.core.MonitorContextProvider
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.Charset
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

internal object MonitorBodyFormatter {

    private val documentBuilderFactory by lazy {
        DocumentBuilderFactory.newInstance().apply {
            isExpandEntityReferences = false
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        }
    }

    private val transformerFactory by lazy {
        TransformerFactory.newInstance().apply {
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        }
    }

    fun formatBytes(bytes: Long): String {
        return Formatter.formatFileSize(MonitorContextProvider.requireApplication(), bytes)
    }

    fun formatBody(
        body: String?,
        contentType: String,
        contentLength: Long
    ): String {
        return when {
            body == null -> ""
            body.isBlank() -> {
                if (contentLength > MonitorConstants.MAX_BODY_BYTES) {
                    ContextCompat.getString(
                        MonitorContextProvider.requireApplication(),
                        R.string.monitor_body_too_large
                    )
                } else {
                    ContextCompat.getString(
                        MonitorContextProvider.requireApplication(),
                        R.string.monitor_encoded_body_omitted
                    )
                }
            }

            isJsonContent(contentType = contentType, body = body) -> {
                MonitorJsonFormatter.toPrettyJson(json = body)
            }

            contentType.contains(other = "xml", ignoreCase = true) -> {
                formatXml(xml = body)
            }

            else -> body
        }
    }

    private fun isJsonContent(contentType: String, body: String): Boolean {
        if (contentType.contains(other = "json", ignoreCase = true)) {
            return true
        }
        val trimmed = body.trimStart()
        return trimmed.startsWith(prefix = "{") || trimmed.startsWith(prefix = "[")
    }

    private fun formatXml(xml: String): String {
        return try {
            val documentBuilder: DocumentBuilder = documentBuilderFactory.newDocumentBuilder()
            val inputSource =
                InputSource(ByteArrayInputStream(xml.toByteArray(Charset.defaultCharset())))
            val document: Document = documentBuilder.parse(inputSource)
            val domSource = DOMSource(document)
            val writer = StringWriter()
            val result = StreamResult(writer)
            transformerFactory.newTransformer().apply {
                setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                setOutputProperty(OutputKeys.INDENT, "yes")
                transform(domSource, result)
            }
            writer.toString()
        } catch (_: SAXParseException) {
            xml
        } catch (_: IOException) {
            xml
        } catch (_: TransformerException) {
            xml
        }
    }

}
