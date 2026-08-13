package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastOpenedTimestamp DESC LIMIT 10")
    fun getAllRecentFiles(): Flow<List<RecentFileEntity>>

    @Query("SELECT * FROM recent_files WHERE uriString = :uriString LIMIT 1")
    suspend fun getFileByUri(uriString: String): RecentFileEntity?

    @Query("SELECT * FROM recent_files WHERE isStarred = 1 ORDER BY lastOpenedTimestamp DESC")
    fun getStarredFiles(): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE uriString NOT IN (SELECT uriString FROM (SELECT uriString FROM recent_files ORDER BY lastOpenedTimestamp DESC LIMIT 10))")
    suspend fun trimToMaxTen()

    @Query("UPDATE recent_files SET isStarred = :isStarred WHERE uriString = :uriString")
    suspend fun setStarred(uriString: String, isStarred: Boolean)

    @Query("UPDATE recent_files SET folderCategory = :folderName WHERE uriString = :uriString")
    suspend fun updateFolderCategory(uriString: String, folderName: String)

    @Query("DELETE FROM recent_files WHERE uriString = :uriString")
    suspend fun deleteByUri(uriString: String)

    @Query("DELETE FROM recent_files WHERE uriString LIKE 'asset://sample_%'")
    suspend fun deleteSampleFiles()

    @Query("DELETE FROM recent_files")
    suspend fun clearAll()
}

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder_categories ORDER BY displayOrder ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folder_categories WHERE isVisible = 1 ORDER BY displayOrder ASC")
    fun getVisibleFolders(): Flow<List<FolderEntity>>

    @Query("SELECT COUNT(*) FROM folder_categories")
    suspend fun getFolderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<FolderEntity>)

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("DELETE FROM folder_categories WHERE id = :id")
    suspend fun deleteFolder(id: Int)

    @Query("DELETE FROM folder_categories")
    suspend fun deleteAllFolders()
}
