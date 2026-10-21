package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PorashonaDao {
    @Query("SELECT * FROM saved_lessons ORDER BY timestamp DESC")
    fun getAllSavedLessons(): Flow<List<SavedLessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: SavedLessonEntity): Long

    @Query("DELETE FROM saved_lessons WHERE id = :id")
    suspend fun deleteLesson(id: Long)

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT 20")
    fun getQuizHistory(): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizHistoryEntity): Long

    @Query("SELECT * FROM revision_schedule WHERE isCompleted = 0 ORDER BY timestamp ASC")
    fun getActiveRevisionItems(): Flow<List<RevisionItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisionItem(item: RevisionItemEntity): Long

    @Query("UPDATE revision_schedule SET isCompleted = 1 WHERE id = :id")
    suspend fun markRevisionCompleted(id: Long)

    @Query("DELETE FROM revision_schedule WHERE id = :id")
    suspend fun deleteRevisionItem(id: Long)
}
