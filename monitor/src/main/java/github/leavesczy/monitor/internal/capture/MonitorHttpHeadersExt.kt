package github.leavesczy.monitor.internal.capture

import github.leavesczy.monitor.internal.db.MonitorHttpHeader
import okhttp3.Headers

internal fun Headers.isGzipEncoded(): Boolean {
    return this["Content-Encoding"].equals(other = "gzip", ignoreCase = true)
}

internal fun Headers.hasUnknownContentEncoding(): Boolean {
    val contentEncoding = this["Content-Encoding"] ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
}

internal fun Headers.toMonitorHeaders(): List<MonitorHttpHeader> {
    return map { (headerName, headerValue) ->
        MonitorHttpHeader(name = headerName, value = headerValue)
    }
}