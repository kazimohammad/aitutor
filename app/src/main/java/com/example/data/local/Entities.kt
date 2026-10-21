package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_lessons")
data class SavedLessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicName: String,
    val subjectName: String,
    val className: String,
    val summary: String,
    val simpleExplanation: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val topicName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val percentage: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "revision_schedule")
data class RevisionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicName: String,
    val subjectName: String,
    val difficulty: String, // "Easy", "Needs Practice", "Difficult"
    val nextReviewDate: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
