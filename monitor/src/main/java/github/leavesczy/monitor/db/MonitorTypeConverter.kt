package github.leavesczy.monitor.db

import androidx.room.TypeConverter
import github.leavesczy.monitor.provider.JsonProvider

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorTypeConverter {

    @TypeConverter
    fun fromJsonArray(json: String): List<MonitorHeader> {
        return JsonProvider.fromJsonArray(json, MonitorHeader::class.java)
    }

    @TypeConverter
    fun toJson(list: List<MonitorHeader>): String {
        return JsonProvider.toJson(list)
    }

}