package github.leavesczy.monitor.internal.db

import androidx.room3.ColumnTypeConverter
import github.leavesczy.monitor.internal.format.MonitorJsonFormatter

internal class MonitorTypeConverter {

    @ColumnTypeConverter
    fun fromJsonArray(json: String): List<MonitorHttpHeader> {
        return MonitorJsonFormatter.fromJsonArray(
            json = json,
            clazz = MonitorHttpHeader::class.java
        )
    }

    @ColumnTypeConverter
    fun toJson(list: List<MonitorHttpHeader>): String {
        return MonitorJsonFormatter.toJson(value = list)
    }

}