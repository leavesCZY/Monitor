package github.leavesczy.monitor.internal.db

import android.text.format.Formatter
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
 * @Date: 2025/7/25 14:09
 * @Desc:
 */
internal fun formatBytes(bytes: Long): String {
    return Formatter.formatFileSize(ContextProvider.context, bytes)
}

internal fun formatBody(body: String?, contentType: String): String {
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
    } catch (_: SAXParseException) {
        xml
    } catch (_: IOException) {
        xml
    } catch (_: TransformerException) {
        xml
    }
}