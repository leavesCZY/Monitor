package github.leavesczy.monitor.internal.ui.export

import github.leavesczy.monitor.internal.db.MonitorHttpHeader
import github.leavesczy.monitor.internal.db.MonitorHttpState
import github.leavesczy.monitor.internal.db.MonitorRecord
import github.leavesczy.monitor.internal.format.MonitorBodyFormatter
import github.leavesczy.monitor.internal.format.MonitorDateTimeFormatter
import github.leavesczy.monitor.internal.ui.model.httpState
import github.leavesczy.monitor.internal.ui.model.pathWithQuery
import github.leavesczy.monitor.internal.ui.model.requestBodyFormatted
import github.leavesczy.monitor.internal.ui.model.requestDurationFormatted
import github.leavesczy.monitor.internal.ui.model.responseBodyFormatted
import github.leavesczy.monitor.internal.ui.model.totalSizeFormatted
import github.leavesczy.monitor.internal.ui.viewmodel.MonitorDetailViewState

internal object MonitorOverviewBuilder {

    fun buildDetailViewState(
        record: MonitorRecord,
        overviewLabel: String,
        requestLabel: String,
        responseLabel: String
    ): MonitorDetailViewState {
        return MonitorDetailViewState(
            title = record.method + " " + record.pathWithQuery,
            tabs = listOf(overviewLabel, requestLabel, responseLabel),
            overview = buildOverview(record = record),
            requestHeaders = record.requestHeaders,
            requestBodyFormatted = record.requestBodyFormatted,
            responseHeaders = record.responseHeaders,
            responseBodyFormatted = record.responseBodyFormatted
        )
    }

    fun buildShareText(record: MonitorRecord): String {
        return buildString {
            append(buildOverview(record = record).format())
            append("\n\n")
            append("----------Request----------")
            append("\n\n")
            append(record.requestHeaders.format())
            if (record.requestBodyFormatted.isNotBlank()) {
                append("\n\n")
                append(record.requestBodyFormatted)
            }
            append("\n\n")
            append("----------Response----------")
            append("\n\n")
            append(record.responseHeaders.format())
            append("\n\n")
            append(record.responseBodyFormatted)
        }
    }

    private fun buildOverview(record: MonitorRecord): List<MonitorHttpHeader> {
        val responseSummaryText = when (record.httpState) {
            MonitorHttpState.Requesting -> ""
            MonitorHttpState.Completed -> "${record.responseCode} ${record.responseMessage}"
            MonitorHttpState.Failed -> record.error ?: ""
        }
        return buildList {
            add(MonitorHttpHeader(name = "Url", value = record.url))
            add(MonitorHttpHeader(name = "Method", value = record.method))
            add(MonitorHttpHeader(name = "Protocol", value = record.protocol))
            add(MonitorHttpHeader(name = "State", value = record.httpState.toString()))
            add(MonitorHttpHeader(name = "Response", value = responseSummaryText))
            add(MonitorHttpHeader(name = "TlsVersion", value = record.responseTlsVersion))
            add(MonitorHttpHeader(name = "CipherSuite", value = record.responseCipherSuite))
            add(
                MonitorHttpHeader(
                    name = "Request Time",
                    value = MonitorDateTimeFormatter.formatForDetail(timestamp = record.requestTime)
                )
            )
            add(
                MonitorHttpHeader(
                    name = "Response Time",
                    value = MonitorDateTimeFormatter.formatForDetail(timestamp = record.responseTime)
                )
            )
            add(MonitorHttpHeader(name = "Duration", value = record.requestDurationFormatted))
            add(
                MonitorHttpHeader(
                    name = "Request Size",
                    value = MonitorBodyFormatter.formatBytes(bytes = record.requestContentLength)
                )
            )
            add(
                MonitorHttpHeader(
                    name = "Response Size",
                    value = MonitorBodyFormatter.formatBytes(bytes = record.responseContentLength)
                )
            )
            add(MonitorHttpHeader(name = "Total Size", value = record.totalSizeFormatted))
        }
    }

    private fun List<MonitorHttpHeader>.format(): String {
        return buildString {
            this@format.forEachIndexed { index, header ->
                append(header.name)
                append(" : ")
                append(header.value)
                if (index != this@format.lastIndex) {
                    append("\n")
                }
            }
        }
    }

}
