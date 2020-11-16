package github.leavesc.monitor.holder

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.internal.bind.DateTypeAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * 作者：leavesC
 * 时间：2020/11/8 15:34
 * 描述：
 * GitHub：https://github.com/leavesC
 */
internal object SerializableHolder {

    private var gson = GsonBuilder()
        .setPrettyPrinting()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .create()

    fun setPrettyPrinting(json: String): String {
        return try {
            gson.toJson(JsonParser.parseString(json))
        } catch (e: Exception) {
            json
        }
    }

    fun toJson(ob: Any): String {
        return gson.toJson(ob)
    }

    fun <T : Any> fromJson(json: String, t: Class<T>): T {
        return gson.fromJson(json, t)
    }

    fun <T> fromJson(json: String, t: Type): T {
        return gson.fromJson(json, t)
    }

    fun <T> fromJsonArray(json: String, clazz: Class<T>): List<T> {
        val type = ParameterizedTypeImpl(clazz)
        var ob: List<T>? = gson.fromJson<List<T>>(json, type)
        if (ob == null) {
            ob = ArrayList()
        }
        return ob
    }

    private class ParameterizedTypeImpl<T> constructor(val clazz: Class<T>) : ParameterizedType {

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