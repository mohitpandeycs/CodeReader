package com.example.model

import androidx.compose.ui.graphics.Color

enum class LanguageCategory {
    NOTEBOOKS,
    SCRIPTS,
    DATA_FILES,
    DOCUMENTS,
    WEB,
    CONFIG
}

data class FileTypeInfo(
    val extension: String,
    val name: String,
    val category: LanguageCategory,
    val mimeType: String,
    val accentColor: Color,
    val isNotebook: Boolean = false
)
