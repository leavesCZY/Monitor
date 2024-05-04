package github.leavesczy.monitor.internal

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:34
 * @Desc:
 */
internal object JsonFormat {

    private val gson by lazy(mode = LazyThreadSafetyMode.NONE) {
        GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create()
    }

    private val prettyPrintingGson by lazy(mode = LazyThreadSafetyMode.NONE) {
        gson.newBuilder()
            .setPrettyPrinting()
            .create()
    }

    fun toJson(ob: Any): String {
        return gson.toJson(ob)
    }

    fun toPrettyJson(json: String): String {
        return try {
            prettyPrintingGson.toJson(JsonParser.parseString(json))
        } catch (e: JsonParseException) {
            e.printStackTrace()
            json
        }
    }

    fun <T> fromJsonArray(json: String, clazz: Class<T>): List<T> {
        val type = ParameterizedTypeImpl(clazz = clazz)
        return gson.fromJson(json, type) ?: emptyList()
    }

    private class ParameterizedTypeImpl<T>(val clazz: Class<T>) : ParameterizedType {

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(clazz)
        }

        override fun getRawType(): Type {
            return List::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }

}