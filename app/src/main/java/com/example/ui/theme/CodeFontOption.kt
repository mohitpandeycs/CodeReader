package com.example.ui.theme

import androidx.compose.ui.text.font.FontFamily

enum class CodeFontOption(
    val id: String,
    val displayName: String,
    val description: String,
    val sampleSnippet: String = "fun main() {\n    val greeting = \"Hello CodeReader\"\n    println(greeting)\n}"
) {
    JETBRAINS_MONO("jetbrains", "JetBrains Mono", "High legibility developer monospace font"),
    FIRA_CODE("fira", "Fira Code", "Clean monospace with code ligature style"),
    SOURCE_CODE_PRO("source", "Source Code Pro", "Balanced width monospace by Adobe"),
    IBM_PLEX_MONO("ibm", "IBM Plex Mono", "Technical serif-accented monospace");

    val fontFamily: FontFamily
        get() = when (this) {
            JETBRAINS_MONO -> FontFamily.Monospace
            FIRA_CODE -> FontFamily(android.graphics.Typeface.create("sans-serif-monospace", android.graphics.Typeface.NORMAL))
            SOURCE_CODE_PRO -> FontFamily(android.graphics.Typeface.create("monospace", android.graphics.Typeface.BOLD))
            IBM_PLEX_MONO -> FontFamily(android.graphics.Typeface.create("serif-monospace", android.graphics.Typeface.NORMAL))
        }
}
