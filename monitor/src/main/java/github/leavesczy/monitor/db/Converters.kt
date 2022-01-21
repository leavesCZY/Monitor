package github.leavesczy.monitor.db

import androidx.room.TypeConverter
import github.leavesczy.monitor.holder.SerializableHolder
import java.util.*

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:43
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toHttpHeaderList(json: String): List<HttpHeader> {
        return SerializableHolder.fromJsonArray(json, HttpHeader::class.java)
    }

    @TypeConverter
    fun toJsonFromHttpHeaderList(list: List<HttpHeader>): String {
        return SerializableHolder.toJson(list)
    }

}