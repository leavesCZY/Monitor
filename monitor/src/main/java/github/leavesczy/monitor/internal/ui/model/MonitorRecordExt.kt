package github.leavesczy.monitor.internal.ui.model

import github.leavesczy.monitor.internal.db.MonitorHttpState
import github.leavesczy.monitor.internal.db.MonitorRecord
import github.leavesczy.monitor.internal.format.MonitorBodyFormatter
import github.leavesczy.monitor.internal.format.MonitorDateTimeFormatter

internal val MonitorRecord.pathWithQuery: String
    get() = if (query.isBlank()) path else "$path?$query"

internal val MonitorRecord.httpState: MonitorHttpState
    get() = when {
        error != null -> MonitorHttpState.Failed
        responseCode == MonitorRecord.PENDING_RESPONSE_CODE -> MonitorHttpState.Requesting
        else -> MonitorHttpState.Completed
    }

internal val MonitorRecord.notificationText: String
    get() = when (httpState) {
        MonitorHttpState.Requesting -> "...$pathWithQuery"
        MonitorHttpState.Completed -> "$responseCode $pathWithQuery"
        MonitorHttpState.Failed -> "!!!$pathWithQuery"
    }

internal val MonitorRecord.responseCodeFormatted: String
    get() = when (httpState) {
        MonitorHttpState.Requesting -> "..."
        MonitorHttpState.Completed -> responseCode.toString()
        MonitorHttpState.Failed -> "!!!"
    }

internal val MonitorRecord.requestTimeFormatted: String
    get() = MonitorDateTimeFormatter.formatForList(timestamp = requestTime)

internal val MonitorRecord.requestDurationFormatted: String
    get() {
        if (requestTime <= 0L || responseTime <= 0L) {
            return ""
        }
        return when (httpState) {
            MonitorHttpState.Requesting -> ""
            MonitorHttpState.Completed,
            MonitorHttpState.Failed -> "${responseTime - requestTime} ms"
        }
    }

internal val MonitorRecord.totalSizeFormatted: String
    get() = when (httpState) {
        MonitorHttpState.Requesting,
        MonitorHttpState.Failed -> ""

        MonitorHttpState.Completed -> {
            MonitorBodyFormatter.formatBytes(bytes = requestContentLength + responseContentLength)
        }
    }

internal val MonitorRecord.requestBodyFormatted: String
    get() = MonitorBodyFormatter.formatBody(
        body = requestBody,
        contentType = requestContentType,
        contentLength = requestContentLength
    )

internal val MonitorRecord.responseBodyFormatted: String
    get() = MonitorBodyFormatter.formatBody(
        body = responseBody,
        contentType = responseContentType,
        contentLength = responseContentLength
    )
