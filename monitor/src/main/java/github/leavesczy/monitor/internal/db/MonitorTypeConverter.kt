package github.leavesczy.monitor.internal.db

import androidx.room.TypeConverter
import github.leavesczy.monitor.internal.JsonFormat

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 */
internal class MonitorTypeConverter {

    @TypeConverter
    fun fromJsonArray(json: String): List<MonitorHttpHeader> {
        return JsonFormat.fromJsonArray(json, MonitorHttpHeader::class.java)
    }

    @TypeConverter
    fun toJson(list: List<MonitorHttpHeader>): String {
        return JsonFormat.toJson(list)
    }

}