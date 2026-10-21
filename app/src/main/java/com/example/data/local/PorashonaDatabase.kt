package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedLessonEntity::class,
        QuizHistoryEntity::class,
        RevisionItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PorashonaDatabase : RoomDatabase() {
    abstract fun porashonaDao(): PorashonaDao

    companion object {
        @Volatile
        private var INSTANCE: PorashonaDatabase? = null

        fun getDatabase(context: Context): PorashonaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PorashonaDatabase::class.java,
                    "porashona_ai.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
