package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val sessionType: String, // "FOCUS" or "BREAK"
    val associatedTaskTitle: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val dateKey: String // YYYY-MM-DD format for fast grouping
)
