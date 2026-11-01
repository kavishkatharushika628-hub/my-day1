package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.util.DateUtils

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["originalDateKey"]),
        Index(value = ["isCompleted"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "General",
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val originalDateKey: String = DateUtils.getTodayKey(),
    val completedAt: Long? = null,
    val completedDateKey: String? = null,
    val notes: String = ""
)
