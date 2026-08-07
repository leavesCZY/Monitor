package github.leavesczy.monitor.internal.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = MonitorDatabase.MONITOR_TABLE_NAME)
internal data class MonitorRecord(
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
    val requestHeaders: List<MonitorHttpHeader>,
    @ColumnInfo(name = "requestBody")
    val requestBody: String?,
    @ColumnInfo(name = "requestContentType")
    val requestContentType: String,
    @ColumnInfo(name = "requestContentLength")
    val requestContentLength: Long,
    @ColumnInfo(name = "requestTime")
    val requestTime: Long,
    @ColumnInfo(name = "responseHeaders")
    val responseHeaders: List<MonitorHttpHeader>,
    @ColumnInfo(name = "responseBody")
    val responseBody: String?,
    @ColumnInfo(name = "responseContentType")
    val responseContentType: String,
    @ColumnInfo(name = "responseContentLength")
    val responseContentLength: Long,
    @ColumnInfo(name = "responseTime")
    val responseTime: Long,
    @ColumnInfo(name = "responseTlsVersion")
    val responseTlsVersion: String,
    @ColumnInfo(name = "responseCipherSuite")
    val responseCipherSuite: String,
    @ColumnInfo(name = "responseCode")
    val responseCode: Int = PENDING_RESPONSE_CODE,
    @ColumnInfo(name = "responseMessage")
    val responseMessage: String,
    @ColumnInfo(name = "error")
    val error: String?
) {

    companion object {
        const val PENDING_RESPONSE_CODE = -1
    }

}

internal data class MonitorHttpHeader(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "value") val value: String
)

internal enum class MonitorHttpState {
    Requesting,
    Completed,
    Failed;
}
