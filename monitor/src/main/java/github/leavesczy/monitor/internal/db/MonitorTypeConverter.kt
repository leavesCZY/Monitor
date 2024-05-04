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
    fun fromJsonArray(json: String): List<MonitorPair> {
        return JsonFormat.fromJsonArray(json, MonitorPair::class.java)
    }

    @TypeConverter
    fun toJson(list: List<MonitorPair>): String {
        return JsonFormat.toJson(list)
    }

}