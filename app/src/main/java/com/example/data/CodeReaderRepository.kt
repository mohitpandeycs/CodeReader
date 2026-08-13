package com.example.data

import kotlinx.coroutines.flow.Flow

class CodeReaderRepository(
    private val recentFileDao: RecentFileDao,
    private val folderDao: FolderDao
) {
    val allRecentFiles: Flow<List<RecentFileEntity>> = recentFileDao.getAllRecentFiles()
    val allFolders: Flow<List<FolderEntity>> = folderDao.getAllFolders()
    val visibleFolders: Flow<List<FolderEntity>> = folderDao.getVisibleFolders()

    suspend fun addOrUpdateRecentFile(file: RecentFileEntity) {
        val existing = recentFileDao.getFileByUri(file.uriString)
        val toSave = if (existing != null) {
            existing.copy(
                lastOpenedTimestamp = System.currentTimeMillis(),
                fileSize = if (file.fileSize.isNotEmpty()) file.fileSize else existing.fileSize,
                lineCount = if (file.lineCount.isNotEmpty()) file.lineCount else existing.lineCount,
                contentPreview = if (file.contentPreview.isNotEmpty()) file.contentPreview else existing.contentPreview
            )
        } else {
            file.copy(lastOpenedTimestamp = System.currentTimeMillis())
        }
        recentFileDao.insertOrUpdate(toSave)
        recentFileDao.trimToMaxTen()
    }

    suspend fun toggleStar(uriString: String, isStarred: Boolean) {
        recentFileDao.setStarred(uriString, isStarred)
    }

    suspend fun updateFileFolder(uriString: String, folderName: String) {
        recentFileDao.updateFolderCategory(uriString, folderName)
    }

    suspend fun addFolder(folder: FolderEntity) {
        folderDao.insertFolder(folder)
    }

    suspend fun deleteRecentFile(uriString: String) {
        recentFileDao.deleteByUri(uriString)
    }

    suspend fun updateFolder(folder: FolderEntity) {
        folderDao.updateFolder(folder)
    }

    suspend fun saveFolderList(folders: List<FolderEntity>) {
        folderDao.insertFolders(folders)
    }

    suspend fun resetDefaultFolders() {
        folderDao.deleteAllFolders()
        folderDao.insertFolders(getDefaultFolders())
    }

    suspend fun initializeDefaultsIfEmpty() {
        if (folderDao.getFolderCount() == 0) {
            val defaultFoldersList = getDefaultFolders()
            folderDao.insertFolders(defaultFoldersList)
        }
        recentFileDao.deleteSampleFiles()
    }

    companion object {
        fun getDefaultFolders(): List<FolderEntity> {
            return listOf(
                FolderEntity(id = 1, name = "Notebooks", colorHex = "#E8A87C", isVisible = true, displayOrder = 0),
                FolderEntity(id = 2, name = "Scripts", colorHex = "#4A7FF2", isVisible = true, displayOrder = 1),
                FolderEntity(id = 3, name = "Data Files", colorHex = "#C1C6D7", isVisible = false, displayOrder = 2),
                FolderEntity(id = 4, name = "Archive", colorHex = "#3E3E3E", isVisible = false, displayOrder = 3)
            )
        }
    }
}
