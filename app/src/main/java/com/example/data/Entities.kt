package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val uriString: String,
    val fileName: String,
    val extension: String,
    val fileSize: String,
    val lineCount: String,
    val lastOpenedTimestamp: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false,
    val folderCategory: String = "Scripts",
    val contentPreview: String = ""
)

@Entity(tableName = "folder_categories")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val isVisible: Boolean = true,
    val displayOrder: Int = 0
)
