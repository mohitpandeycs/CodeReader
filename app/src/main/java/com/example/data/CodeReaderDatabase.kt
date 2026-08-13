package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentFileEntity::class, FolderEntity::class], version = 1, exportSchema = false)
abstract class CodeReaderDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var INSTANCE: CodeReaderDatabase? = null

        fun getDatabase(context: Context): CodeReaderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeReaderDatabase::class.java,
                    "codereader_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
