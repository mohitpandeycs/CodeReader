package com.example.syntax

import androidx.compose.ui.graphics.Color

enum class SyntaxThemeType(val displayName: String, val isDark: Boolean) {
    DARK_PLUS("Dark+ (Default)", true),
    LIGHT_MODERN("Light Modern", false),
    MONOKAI("Monokai", true)
}

data class SyntaxColors(
    val backgroundColor: Color,
    val textColor: Color,
    val lineNumberColor: Color,
    val keywordColor: Color,
    val stringColor: Color,
    val numberColor: Color,
    val functionColor: Color,
    val commentColor: Color,
    val operatorColor: Color,
    val variableColor: Color,
    val cardBorderColor: Color
)

object SyntaxThemes {
    val DarkPlus = SyntaxColors(
        backgroundColor = Color(0xFF2D2A2E),
        textColor = Color(0xFFFCFCFA),
        lineNumberColor = Color(0xFF727072),
        keywordColor = Color(0xFFFF6188),
        stringColor = Color(0xFFA9DC76),
        numberColor = Color(0xFFAB9DF2),
        functionColor = Color(0xFF78DCE8),
        commentColor = Color(0xFF727072),
        operatorColor = Color(0xFFFF6188),
        variableColor = Color(0xFFFCFCFA),
        cardBorderColor = Color(0x33717786)
    )

    val LightModern = SyntaxColors(
        backgroundColor = Color(0xFFFAFAFA),
        textColor = Color(0xFF24292E),
        lineNumberColor = Color(0xFFA0A0A0),
        keywordColor = Color(0xFFD73A49),
        stringColor = Color(0xFF032F62),
        numberColor = Color(0xFF005CC5),
        functionColor = Color(0xFF6F42C1),
        commentColor = Color(0xFF6A737D),
        operatorColor = Color(0xFFD73A49),
        variableColor = Color(0xFF24292E),
        cardBorderColor = Color(0x33C1C6D7)
    )

    val Monokai = SyntaxColors(
        backgroundColor = Color(0xFF272822),
        textColor = Color(0xFFF8F8F2),
        lineNumberColor = Color(0xFF90908A),
        keywordColor = Color(0xFFF92672),
        stringColor = Color(0xFFE6DB74),
        numberColor = Color(0xFFAE81FF),
        functionColor = Color(0xFFA6E22E),
        commentColor = Color(0xFF75715E),
        operatorColor = Color(0xFFF92672),
        variableColor = Color(0xFFF8F8F2),
        cardBorderColor = Color(0x33717786)
    )

    fun getColors(type: SyntaxThemeType): SyntaxColors {
        return when (type) {
            SyntaxThemeType.DARK_PLUS -> DarkPlus
            SyntaxThemeType.LIGHT_MODERN -> LightModern
            SyntaxThemeType.MONOKAI -> Monokai
        }
    }
}
