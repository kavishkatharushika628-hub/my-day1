package com.example.data

import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allRecordedDateKeys: Flow<List<String>> = taskDao.getAllRecordedDateKeys()

    fun getTasksForOriginalDate(dateKey: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksForOriginalDate(dateKey)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        val safeTask = if (task.originalDateKey.isBlank()) {
            task.copy(originalDateKey = DateUtils.getTodayKey())
        } else {
            task
        }
        return taskDao.insertTask(safeTask)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun deleteCompletedTasksForDate(dateKey: String) {
        taskDao.deleteCompletedTasksForDate(dateKey)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    suspend fun deleteSampleTasks(sampleTitles: List<String>) {
        if (sampleTitles.isNotEmpty()) {
            taskDao.deleteTasksByTitles(sampleTitles)
        }
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val willBeCompleted = !task.isCompleted
        val now = System.currentTimeMillis()
        val todayKey = DateUtils.getTodayKey()

        val updated = task.copy(
            isCompleted = willBeCompleted,
            completedAt = if (willBeCompleted) now else null,
            completedDateKey = if (willBeCompleted) todayKey else null
        )
        taskDao.updateTask(updated)
    }
}
