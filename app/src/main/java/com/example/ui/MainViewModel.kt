package com.example.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CodeReaderDatabase
import com.example.data.CodeReaderRepository
import com.example.data.FolderEntity
import com.example.data.RecentFileEntity
import com.example.model.JupyterNotebook
import com.example.parser.JupyterParser
import com.example.registry.FileRegistry
import com.example.syntax.SyntaxThemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

import com.example.ui.theme.CodeFontOption

data class OpenFileState(
    val uriString: String = "",
    val fileName: String = "",
    val extension: String = "",
    val content: String = "",
    val lines: List<String> = emptyList(),
    val isNotebook: Boolean = false,
    val notebookData: JupyterNotebook? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

data class SettingsState(
    val syntaxTheme: SyntaxThemeType = SyntaxThemeType.DARK_PLUS,
    val enableGlassEffects: Boolean = true,
    val enableWordWrap: Boolean = true,
    val fontSizeSp: Int = 12,
    val codeFontOption: CodeFontOption = CodeFontOption.JETBRAINS_MONO
)

class MainViewModel(
    private val repository: CodeReaderRepository,
    context: Context
) : ViewModel() {

    private val prefs = context.applicationContext.getSharedPreferences("codereader_prefs", Context.MODE_PRIVATE)

    val recentFiles: StateFlow<List<RecentFileEntity>> = repository.allRecentFiles
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val folders: StateFlow<List<FolderEntity>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val visibleFolders: StateFlow<List<FolderEntity>> = repository.visibleFolders
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _openFileState = MutableStateFlow(OpenFileState())
    val openFileState: StateFlow<OpenFileState> = _openFileState.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All Files")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfEmpty()
        }
    }

    private fun loadSettings(): SettingsState {
        val themeName = prefs.getString("syntaxTheme", SyntaxThemeType.DARK_PLUS.name) ?: SyntaxThemeType.DARK_PLUS.name
        val theme = try { SyntaxThemeType.valueOf(themeName) } catch (e: Exception) { SyntaxThemeType.DARK_PLUS }
        val glass = prefs.getBoolean("enableGlassEffects", true)
        val wrap = prefs.getBoolean("enableWordWrap", true)
        val fontSp = prefs.getInt("fontSizeSp", 12)
        val fontId = prefs.getString("codeFontOption", CodeFontOption.JETBRAINS_MONO.id) ?: CodeFontOption.JETBRAINS_MONO.id
        val fontOpt = CodeFontOption.values().find { it.id == fontId } ?: CodeFontOption.JETBRAINS_MONO

        return SettingsState(
            syntaxTheme = theme,
            enableGlassEffects = glass,
            enableWordWrap = wrap,
            fontSizeSp = fontSp,
            codeFontOption = fontOpt
        )
    }

    private fun persistSettings() {
        val state = _settingsState.value
        prefs.edit()
            .putString("syntaxTheme", state.syntaxTheme.name)
            .putBoolean("enableGlassEffects", state.enableGlassEffects)
            .putBoolean("enableWordWrap", state.enableWordWrap)
            .putInt("fontSizeSp", state.fontSizeSp)
            .putString("codeFontOption", state.codeFontOption.id)
            .apply()
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleStar(file: RecentFileEntity) {
        viewModelScope.launch {
            repository.toggleStar(file.uriString, !file.isStarred)
        }
    }

    fun assignFileToFolder(file: RecentFileEntity, folderName: String) {
        viewModelScope.launch {
            repository.updateFileFolder(file.uriString, folderName)
        }
    }

    fun createFolderAndAssignFile(file: RecentFileEntity, folderName: String, colorHex: String) {
        viewModelScope.launch {
            val newFolder = FolderEntity(
                name = folderName,
                colorHex = colorHex,
                isVisible = true,
                displayOrder = (folders.value.maxOfOrNull { it.displayOrder } ?: 0) + 1
            )
            repository.addFolder(newFolder)
            repository.updateFileFolder(file.uriString, folderName)
        }
    }

    fun deleteRecentFile(file: RecentFileEntity) {
        viewModelScope.launch {
            repository.deleteRecentFile(file.uriString)
        }
    }

    // Folder Customization Actions
    fun toggleFolderVisibility(folder: FolderEntity) {
        viewModelScope.launch {
            repository.updateFolder(folder.copy(isVisible = !folder.isVisible))
        }
    }

    fun updateFolderName(folder: FolderEntity, newName: String) {
        viewModelScope.launch {
            repository.updateFolder(folder.copy(name = newName))
        }
    }

    fun updateFolderColor(folder: FolderEntity, colorHex: String) {
        viewModelScope.launch {
            repository.updateFolder(folder.copy(colorHex = colorHex))
        }
    }

    fun reorderFolders(reorderedList: List<FolderEntity>) {
        viewModelScope.launch {
            val updated = reorderedList.mapIndexed { index, item -> item.copy(displayOrder = index) }
            repository.saveFolderList(updated)
        }
    }

    fun resetFoldersToDefault() {
        viewModelScope.launch {
            repository.resetDefaultFolders()
        }
    }

    // Settings Actions
    fun updateSyntaxTheme(theme: SyntaxThemeType) {
        _settingsState.value = _settingsState.value.copy(syntaxTheme = theme)
        persistSettings()
    }

    fun toggleGlassEffects(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(enableGlassEffects = enabled)
        persistSettings()
    }

    fun toggleWordWrap(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(enableWordWrap = enabled)
        persistSettings()
    }

    fun updateFontSize(sizeSp: Int) {
        _settingsState.value = _settingsState.value.copy(fontSizeSp = sizeSp)
        persistSettings()
    }

    fun updateCodeFontOption(option: CodeFontOption) {
        _settingsState.value = _settingsState.value.copy(codeFontOption = option)
        persistSettings()
    }

    fun setInFileSearchQuery(query: String) {
        _openFileState.value = _openFileState.value.copy(searchQuery = query)
    }

    // Load file from Uri or Asset path
    fun openFile(context: Context, uri: Uri, customFileName: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _openFileState.value = OpenFileState(isLoading = true)

            try {
                val uriString = uri.toString()
                var name = customFileName ?: getFileNameFromUri(context, uri) ?: "file.txt"
                val ext = name.substringAfterLast('.', "").lowercase()
                val fileInfo = FileRegistry.getInfoForExtension(ext)

                val content: String = if (uriString.startsWith("asset://")) {
                    val assetPath = uriString.removePrefix("asset://")
                    context.assets.open(assetPath).bufferedReader().use { it.readText() }
                } else {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            reader.readText()
                        }
                    } ?: ""
                }

                val lines = content.lines()
                val lineCountText = if (fileInfo.isNotebook) "Jupyter Notebook" else "${lines.size} lines"
                val fileSizeText = calculateSizeString(content.length.toLong())

                var notebookData: JupyterNotebook? = null
                if (fileInfo.isNotebook) {
                    notebookData = JupyterParser.parse(content)
                }

                _openFileState.value = OpenFileState(
                    uriString = uriString,
                    fileName = name,
                    extension = ext,
                    content = content,
                    lines = lines,
                    isNotebook = fileInfo.isNotebook,
                    notebookData = notebookData,
                    isLoading = false
                )

                // Save to recent files DB
                val folderCat = when {
                    fileInfo.isNotebook -> "Notebooks"
                    ext in listOf("py", "sh", "go", "rs", "kt", "java", "c", "cpp") -> "Scripts"
                    ext in listOf("json", "xml", "csv") -> "Data Files"
                    else -> "Archive"
                }

                val recentEntity = RecentFileEntity(
                    uriString = uriString,
                    fileName = name,
                    extension = ext,
                    fileSize = fileSizeText,
                    lineCount = if (fileInfo.isNotebook) "${notebookData?.cells?.size ?: 0} cells" else lineCountText,
                    lastOpenedTimestamp = System.currentTimeMillis(),
                    folderCategory = folderCat,
                    contentPreview = if (lines.isNotEmpty()) lines.take(3).joinToString(" ") else ""
                )

                repository.addOrUpdateRecentFile(recentEntity)

            } catch (e: Exception) {
                e.printStackTrace()
                _openFileState.value = OpenFileState(
                    isLoading = false,
                    errorMessage = "Failed to load file: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) return cursor.getString(nameIndex)
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }

    private fun calculateSizeString(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
        }
    }
}

class MainViewModelFactory(
    private val repository: CodeReaderRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
