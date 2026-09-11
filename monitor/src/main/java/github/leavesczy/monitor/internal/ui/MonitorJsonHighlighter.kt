package github.leavesczy.monitor.internal.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

internal object MonitorJsonHighlighter {

    fun highlight(
        json: String,
        keyColor: Color,
        valueColor: Color,
        literalColor: Color,
        braceColor: Color,
        punctuationColor: Color
    ): AnnotatedString {
        if (!looksLikeJson(text = json)) {
            return AnnotatedString(text = json)
        }
        return buildAnnotatedString {
            var index = 0
            while (index < json.length) {
                val char = json[index]
                when {
                    char.isWhitespace() -> {
                        withStyle(style = SpanStyle(color = punctuationColor)) {
                            append(char)
                        }
                        index++
                    }

                    char == '"' -> {
                        val stringEnd = endIndexOfJsonString(json = json, startIndex = index)
                        var cursor = stringEnd
                        while (cursor < json.length && json[cursor].isWhitespace()) {
                            cursor++
                        }
                        val isKey = cursor < json.length && json[cursor] == ':'
                        withStyle(
                            style = SpanStyle(
                                color = if (isKey) {
                                    keyColor
                                } else {
                                    valueColor
                                }
                            )
                        ) {
                            append(json.substring(startIndex = index, endIndex = stringEnd))
                        }
                        index = stringEnd
                    }

                    char.isDigit() || isNumberStart(json = json, index = index) -> {
                        val numberEnd = endIndexOfJsonNumber(json = json, startIndex = index)
                        withStyle(style = SpanStyle(color = literalColor)) {
                            append(json.substring(startIndex = index, endIndex = numberEnd))
                        }
                        index = numberEnd
                    }

                    startsWithLiteral(json = json, index = index, literal = "true") -> {
                        withStyle(style = SpanStyle(color = literalColor)) {
                            append("true")
                        }
                        index += 4
                    }

                    startsWithLiteral(json = json, index = index, literal = "false") -> {
                        withStyle(style = SpanStyle(color = literalColor)) {
                            append("false")
                        }
                        index += 5
                    }

                    startsWithLiteral(json = json, index = index, literal = "null") -> {
                        withStyle(style = SpanStyle(color = literalColor)) {
                            append("null")
                        }
                        index += 4
                    }

                    char == '{' || char == '}' || char == '[' || char == ']' -> {
                        withStyle(style = SpanStyle(color = braceColor)) {
                            append(char)
                        }
                        index++
                    }

                    else -> {
                        withStyle(style = SpanStyle(color = punctuationColor)) {
                            append(char)
                        }
                        index++
                    }
                }
            }
        }
    }

    private fun looksLikeJson(text: String): Boolean {
        val trimmed = text.trimStart()
        return trimmed.startsWith(prefix = "{") || trimmed.startsWith(prefix = "[")
    }

    private fun endIndexOfJsonString(json: String, startIndex: Int): Int {
        var index = startIndex + 1
        while (index < json.length) {
            when (json[index]) {
                '\\' -> index += 2
                '"' -> return index + 1
                else -> index++
            }
        }
        return json.length
    }

    private fun isNumberStart(json: String, index: Int): Boolean {
        return json[index] == '-' &&
                index + 1 < json.length &&
                json[index + 1].isDigit()
    }

    private fun endIndexOfJsonNumber(json: String, startIndex: Int): Int {
        var index = startIndex
        if (json[index] == '-') {
            index++
        }
        while (index < json.length) {
            val char = json[index]
            if (char.isDigit() || char == '.' || char == 'e' || char == 'E' || char == '+' || char == '-') {
                index++
            } else {
                break
            }
        }
        return index
    }

    private fun startsWithLiteral(json: String, index: Int, literal: String): Boolean {
        if (!json.startsWith(prefix = literal, startIndex = index)) {
            return false
        }
        val endIndex = index + literal.length
        if (endIndex >= json.length) {
            return true
        }
        val next = json[endIndex]
        return !next.isLetterOrDigit() && next != '_'
    }

}
