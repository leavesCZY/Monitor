package github.leavesczy.monitor.internal.format

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object MonitorJsonFormatter {

    private const val MAX_NESTED_JSON_DEPTH = 8

    private val gson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create()
    }

    private val prettyPrintingGson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()
    }

    fun toJson(value: Any): String {
        return gson.toJson(value)
    }

    fun toPrettyJson(json: String): String {
        return runCatching {
            val element = JsonParser.parseString(json)
            val expanded = expandNestedJsonStrings(element = element)
            prettyPrintingGson.toJson(expanded)
        }.getOrElse { error ->
            if (error is JsonParseException) {
                error.printStackTrace()
            }
            json
        }
    }

    fun <T> fromJsonArray(json: String, clazz: Class<T>): List<T> {
        return runCatching {
            val type = ParameterizedTypeImpl(clazz = clazz)
            gson.fromJson<List<T>>(json, type) ?: emptyList()
        }.getOrElse { _ -> emptyList() }
    }

    /**
     * Expands string values that themselves contain JSON objects/arrays,
     * so nested payloads are shown as real structure instead of escaped text.
     */
    private fun expandNestedJsonStrings(
        element: JsonElement,
        depth: Int = 0
    ): JsonElement {
        if (depth >= MAX_NESTED_JSON_DEPTH) {
            return element
        }
        return when {
            element.isJsonObject -> {
                val result = JsonObject()
                element.asJsonObject.entrySet().forEach { (key, value) ->
                    result.add(key, expandNestedJsonStrings(element = value, depth = depth + 1))
                }
                result
            }

            element.isJsonArray -> {
                val result = JsonArray()
                element.asJsonArray.forEach { item ->
                    result.add(expandNestedJsonStrings(element = item, depth = depth + 1))
                }
                result
            }

            element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                parseNestedJsonString(text = element.asString, depth = depth) ?: element
            }

            else -> element
        }
    }

    private fun parseNestedJsonString(text: String, depth: Int): JsonElement? {
        val trimmed = text.trim()
        val looksLikeObject = trimmed.startsWith(prefix = "{") && trimmed.endsWith(suffix = "}")
        val looksLikeArray = trimmed.startsWith(prefix = "[") && trimmed.endsWith(suffix = "]")
        if (!looksLikeObject && !looksLikeArray) {
            return null
        }
        return runCatching {
            val nested = JsonParser.parseString(trimmed)
            if (!nested.isJsonObject && !nested.isJsonArray) {
                null
            } else {
                expandNestedJsonStrings(element = nested, depth = depth + 1)
            }
        }.getOrNull()
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
