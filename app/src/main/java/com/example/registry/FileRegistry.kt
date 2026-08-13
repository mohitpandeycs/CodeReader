package com.example.registry

import androidx.compose.ui.graphics.Color
import com.example.model.FileTypeInfo
import com.example.model.LanguageCategory

object FileRegistry {

    private val registry: Map<String, FileTypeInfo> = mapOf(
        // Jupyter Notebooks
        "ipynb" to FileTypeInfo("ipynb", "Jupyter Notebook", LanguageCategory.NOTEBOOKS, "application/x-ipynb+json", Color(0xFFE8A87C), isNotebook = true),

        // Python & Scripts
        "py" to FileTypeInfo("py", "Python", LanguageCategory.SCRIPTS, "text/x-python", Color(0xFF007AFF)),
        "sh" to FileTypeInfo("sh", "Shell Script", LanguageCategory.SCRIPTS, "text/x-sh", Color(0xFF4A7FF2)),
        "go" to FileTypeInfo("go", "Go", LanguageCategory.SCRIPTS, "text/x-go", Color(0xFF00ADD8)),
        "rs" to FileTypeInfo("rs", "Rust", LanguageCategory.SCRIPTS, "text/x-rust", Color(0xFFDEA584)),

        // Java, Kotlin & C/C++
        "kt" to FileTypeInfo("kt", "Kotlin", LanguageCategory.SCRIPTS, "text/x-kotlin", Color(0xFF7F52FF)),
        "java" to FileTypeInfo("java", "Java", LanguageCategory.SCRIPTS, "text/x-java", Color(0xFFB07219)),
        "c" to FileTypeInfo("c", "C", LanguageCategory.SCRIPTS, "text/x-c", Color(0xFF555555)),
        "cpp" to FileTypeInfo("cpp", "C++", LanguageCategory.SCRIPTS, "text/x-c++", Color(0xFFF34B7D)),
        "h" to FileTypeInfo("h", "C/C++ Header", LanguageCategory.SCRIPTS, "text/x-c-header", Color(0xFFA8B9CC)),

        // Web & JS/TS
        "js" to FileTypeInfo("js", "JavaScript", LanguageCategory.WEB, "text/javascript", Color(0xFFF1E05A)),
        "jsx" to FileTypeInfo("jsx", "React JSX", LanguageCategory.WEB, "text/jsx", Color(0xFFF1E05A)),
        "ts" to FileTypeInfo("ts", "TypeScript", LanguageCategory.WEB, "text/typescript", Color(0xFF3178C6)),
        "tsx" to FileTypeInfo("tsx", "React TSX", LanguageCategory.WEB, "text/tsx", Color(0xFF3178C6)),
        "html" to FileTypeInfo("html", "HTML", LanguageCategory.WEB, "text/html", Color(0xFFE34C26)),
        "css" to FileTypeInfo("css", "CSS", LanguageCategory.WEB, "text/css", Color(0xFF563D7C)),
        "scss" to FileTypeInfo("scss", "SCSS", LanguageCategory.WEB, "text/x-scss", Color(0xFFC6538C)),

        // Data & Config
        "json" to FileTypeInfo("json", "JSON", LanguageCategory.DATA_FILES, "application/json", Color(0xFF292929)),
        "xml" to FileTypeInfo("xml", "XML", LanguageCategory.DATA_FILES, "text/xml", Color(0xFF006080)),
        "yaml" to FileTypeInfo("yaml", "YAML", LanguageCategory.CONFIG, "text/x-yaml", Color(0xFFCB171E)),
        "yml" to FileTypeInfo("yml", "YAML", LanguageCategory.CONFIG, "text/x-yaml", Color(0xFFCB171E)),
        "csv" to FileTypeInfo("csv", "CSV Data", LanguageCategory.DATA_FILES, "text/csv", Color(0xFFC64F00)),

        // Documents
        "md" to FileTypeInfo("md", "Markdown", LanguageCategory.DOCUMENTS, "text/markdown", Color(0xFF083FA1))
    )

    private val defaultType = FileTypeInfo("txt", "Text File", LanguageCategory.DOCUMENTS, "text/plain", Color(0xFF636363))

    fun getInfoForExtension(ext: String): FileTypeInfo {
        return registry[ext.lowercase().removePrefix(".")] ?: defaultType
    }

    fun getInfoForFileName(fileName: String): FileTypeInfo {
        val ext = fileName.substringAfterLast('.', "")
        return getInfoForExtension(ext)
    }

    fun getAllSupportedExtensions(): List<String> = registry.keys.toList()
}
