package com.example.parser

import com.example.model.CellOutput
import com.example.model.CellType
import com.example.model.JupyterNotebook
import com.example.model.NotebookCell
import org.json.JSONArray
import org.json.JSONObject

object JupyterParser {

    fun parse(jsonContent: String): JupyterNotebook {
        return try {
            val root = JSONObject(jsonContent)
            
            // Extract kernel info
            var kernelName = "Python 3"
            if (root.has("metadata")) {
                val meta = root.optJSONObject("metadata")
                if (meta != null && meta.has("kernelspec")) {
                    kernelName = meta.optJSONObject("kernelspec")?.optString("display_name") ?: kernelName
                } else if (meta != null && meta.has("language_info")) {
                    val lang = meta.optJSONObject("language_info")?.optString("name")
                    if (!lang.isNullOrBlank()) {
                        kernelName = "${lang.replaceFirstChar { it.uppercase() }} Kernel"
                    }
                }
            }

            val cells = mutableListOf<NotebookCell>()
            val cellsArray = root.optJSONArray("cells") ?: JSONArray()

            for (i in 0 until cellsArray.length()) {
                val cellObj = cellsArray.optJSONObject(i) ?: continue
                val typeStr = cellObj.optString("cell_type", "code")
                val type = when (typeStr) {
                    "markdown" -> CellType.MARKDOWN
                    "raw" -> CellType.RAW
                    else -> CellType.CODE
                }

                val source = parseMultiString(cellObj.opt("source"))
                val executionCount = if (cellObj.has("execution_count") && !cellObj.isNull("execution_count")) {
                    cellObj.optInt("execution_count")
                } else null

                val outputs = mutableListOf<CellOutput>()
                val outputsArray = cellObj.optJSONArray("outputs")
                if (outputsArray != null) {
                    for (j in 0 until outputsArray.length()) {
                        val outObj = outputsArray.optJSONObject(j) ?: continue
                        val outType = outObj.optString("output_type")

                        if (outType == "error") {
                            val ename = outObj.optString("ename", "Error")
                            val evalue = outObj.optString("evalue", "")
                            outputs.add(CellOutput.ErrorOutput(ename, evalue))
                        } else {
                            // Check for image or text in data / text
                            if (outObj.has("data")) {
                                val dataObj = outObj.optJSONObject("data")
                                if (dataObj != null) {
                                    if (dataObj.has("image/png")) {
                                        val imgData = parseMultiString(dataObj.opt("image/png")).replace("\n", "").trim()
                                        outputs.add(CellOutput.ImageOutput("image/png", imgData))
                                    } else if (dataObj.has("image/jpeg")) {
                                        val imgData = parseMultiString(dataObj.opt("image/jpeg")).replace("\n", "").trim()
                                        outputs.add(CellOutput.ImageOutput("image/jpeg", imgData))
                                    } else if (dataObj.has("text/plain")) {
                                        val txt = parseMultiString(dataObj.opt("text/plain"))
                                        if (txt.isNotBlank()) outputs.add(CellOutput.TextOutput(txt))
                                    }
                                }
                            } else if (outObj.has("text")) {
                                val txt = parseMultiString(outObj.opt("text"))
                                if (txt.isNotBlank()) outputs.add(CellOutput.TextOutput(txt))
                            }
                        }
                    }
                }

                cells.add(NotebookCell(type, executionCount, source, outputs))
            }

            JupyterNotebook(kernelName = kernelName, cells = cells)
        } catch (e: Exception) {
            e.printStackTrace()
            JupyterNotebook(
                kernelName = "Error Reading Notebook",
                cells = listOf(
                    NotebookCell(
                        type = CellType.MARKDOWN,
                        source = "### Error Loading Notebook\nFailed to parse JSON content: ${e.localizedMessage}"
                    )
                )
            )
        }
    }

    private fun parseMultiString(obj: Any?): String {
        return when (obj) {
            is String -> obj
            is JSONArray -> {
                val sb = StringBuilder()
                for (k in 0 until obj.length()) {
                    sb.append(obj.optString(k))
                }
                sb.toString()
            }
            else -> ""
        }
    }
}
