package com.example.model

data class JupyterNotebook(
    val kernelName: String = "Python 3",
    val cells: List<NotebookCell> = emptyList()
)

enum class CellType {
    CODE,
    MARKDOWN,
    RAW
}

data class NotebookCell(
    val type: CellType,
    val executionCount: Int? = null,
    val source: String,
    val outputs: List<CellOutput> = emptyList()
)

sealed class CellOutput {
    data class TextOutput(val text: String) : CellOutput()
    data class ImageOutput(val mimeType: String, val base64Data: String) : CellOutput()
    data class ErrorOutput(val ename: String, val evalue: String) : CellOutput()
}
