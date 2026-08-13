package com.example.syntax

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Pattern

object SyntaxHighlighter {

    private val KEYWORDS = setOf(
        "abstract", "and", "as", "assert", "async", "await", "break", "case", "catch", "class",
        "const", "continue", "def", "delegate", "delete", "do", "elif", "else", "enum", "except",
        "export", "extends", "false", "final", "finally", "fn", "for", "from", "func", "function",
        "if", "import", "in", "inline", "instanceof", "interface", "is", "lambda", "let", "match",
        "mut", "native", "new", "nil", "not", "null", "object", "or", "override", "package", "pass",
        "private", "protected", "pub", "public", "raise", "return", "sealed", "self", "static",
        "struct", "super", "switch", "synchronized", "this", "throw", "true", "try", "type",
        "typealias", "typeof", "val", "var", "void", "while", "with", "yield"
    )

    fun highlightLine(
        line: String,
        extension: String,
        colors: SyntaxColors,
        searchQuery: String = ""
    ): AnnotatedString {
        if (line.isEmpty()) return AnnotatedString("")

        return buildAnnotatedString {
            append(line)

            // Default style for line
            addStyle(SpanStyle(color = colors.textColor), 0, line.length)

            val ext = extension.lowercase().removePrefix(".")

            when (ext) {
                "md" -> highlightMarkdown(line, colors)
                "json" -> highlightJson(line, colors)
                else -> highlightCode(line, colors)
            }

            // Highlight search query hits if present
            if (searchQuery.isNotBlank() && searchQuery.length >= 2) {
                var index = line.indexOf(searchQuery, ignoreCase = true)
                while (index >= 0) {
                    addStyle(
                        SpanStyle(
                            background = colors.keywordColor.copy(alpha = 0.35f),
                            fontWeight = FontWeight.Bold
                        ),
                        index,
                        index + searchQuery.length
                    )
                    index = line.indexOf(searchQuery, index + searchQuery.length, ignoreCase = true)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightCode(line: String, colors: SyntaxColors) {
        val length = line.length

        // 1. Comments: // ... or # ... or /* ... */
        val commentIndex = findCommentIndex(line)
        if (commentIndex >= 0) {
            addStyle(SpanStyle(color = colors.commentColor), commentIndex, length)
        }

        val codeEnd = if (commentIndex >= 0) commentIndex else length

        // 2. Strings: "..." or '...' or `...`
        val stringPattern = Pattern.compile("(\"[^\"]*\")|('[^']*')|(`[^`]*`)")
        val stringMatcher = stringPattern.matcher(line.substring(0, codeEnd))
        while (stringMatcher.find()) {
            addStyle(SpanStyle(color = colors.stringColor), stringMatcher.start(), stringMatcher.end())
        }

        // 3. Numbers: 123, 0x12, 3.14
        val numberPattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b")
        val numberMatcher = numberPattern.matcher(line.substring(0, codeEnd))
        while (numberMatcher.find()) {
            addStyle(SpanStyle(color = colors.numberColor), numberMatcher.start(), numberMatcher.end())
        }

        // 4. Keywords & Function Calls
        val wordPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
        val wordMatcher = wordPattern.matcher(line.substring(0, codeEnd))
        while (wordMatcher.find()) {
            val word = wordMatcher.group()
            val start = wordMatcher.start()
            val end = wordMatcher.end()

            if (KEYWORDS.contains(word)) {
                addStyle(SpanStyle(color = colors.keywordColor, fontWeight = FontWeight.SemiBold), start, end)
            } else if (end < codeEnd && line[end] == '(') {
                addStyle(SpanStyle(color = colors.functionColor, fontWeight = FontWeight.Medium), start, end)
            }
        }
    }

    private fun findCommentIndex(line: String): Int {
        var inString = false
        var stringChar = ' '

        for (i in line.indices) {
            val c = line[i]

            if (inString) {
                if (c == stringChar && (i == 0 || line[i - 1] != '\\')) {
                    inString = false
                }
            } else {
                if (c == '"' || c == '\'') {
                    inString = true
                    stringChar = c
                } else if (c == '#') {
                    return i
                } else if (c == '/' && i + 1 < line.length && (line[i + 1] == '/' || line[i + 1] == '*')) {
                    return i
                }
            }
        }
        return -1
    }

    private fun AnnotatedString.Builder.highlightJson(line: String, colors: SyntaxColors) {
        val keyPattern = Pattern.compile("\"([^\"]+)\"\\s*:")
        val matcher = keyPattern.matcher(line)
        while (matcher.find()) {
            addStyle(SpanStyle(color = colors.functionColor, fontWeight = FontWeight.SemiBold), matcher.start(), matcher.end(1) + 1)
        }
    }

    private fun AnnotatedString.Builder.highlightMarkdown(line: String, colors: SyntaxColors) {
        val trimmed = line.trimStart()
        if (trimmed.startsWith("#")) {
            addStyle(SpanStyle(color = colors.keywordColor, fontWeight = FontWeight.Bold), 0, line.length)
        } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
            addStyle(SpanStyle(color = colors.operatorColor), 0, line.indexOf(trimmed) + 2)
        }
    }
}
