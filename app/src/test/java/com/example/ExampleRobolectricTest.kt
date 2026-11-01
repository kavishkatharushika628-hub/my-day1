package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.TaskDao
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.AppFontSize
import com.example.ui.theme.AppThemeMode
import com.example.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()
        repository = TaskRepository(taskDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context verifies My Day app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("My Day", appName)
    }

    @Test
    fun `fresh installation has completely empty task list and zero initial progress`() = runBlocking {
        val initialTasks = repository.allTasks.first()
        assertTrue("Task list on fresh installation must be empty", initialTasks.isEmpty())
        val todayKey = DateUtils.getTodayKey()
        val todayTasks = repository.getTasksForOriginalDate(todayKey).first()
        assertTrue("Today's task list must be empty", todayTasks.isEmpty())
    }

    @Test
    fun `insert and toggle task completion updates state and sets completedDateKey while preserving originalDateKey`() = runBlocking {
        val yesterdayKey = "2026-10-30"
        val task = TaskEntity(
            title = "Drink 2L of water",
            category = "Health",
            priority = "High",
            originalDateKey = yesterdayKey
        )
        val id = repository.insertTask(task)
        val inserted = taskDao.getTaskById(id)
        assertNotNull(inserted)
        assertEquals("Drink 2L of water", inserted?.title)
        assertEquals(false, inserted?.isCompleted)
        assertEquals(yesterdayKey, inserted?.originalDateKey)

        // Toggle completion
        inserted?.let { repository.toggleTaskCompletion(it) }
        val updated = taskDao.getTaskById(id)
        assertEquals(true, updated?.isCompleted)
        assertNotNull(updated?.completedAt)
        // Original date key must remain intact
        assertEquals(yesterdayKey, updated?.originalDateKey)
        // Completed date key should match today
        assertEquals(DateUtils.getTodayKey(), updated?.completedDateKey)
    }

    @Test
    fun `historical date tasks query reflects completed carried-over task and updates original day progress`() = runBlocking {
        val pastDate = "2026-10-28"
        // Insert 2 tasks on past date
        val id1 = repository.insertTask(
            TaskEntity(
                title = "Task 1",
                originalDateKey = pastDate,
                isCompleted = true,
                completedDateKey = pastDate
            )
        )
        val id2 = repository.insertTask(
            TaskEntity(
                title = "Task 2",
                originalDateKey = pastDate,
                isCompleted = false
            )
        )

        // Verify initial progress: 1 of 2 completed (50%)
        var pastTasks = repository.getTasksForOriginalDate(pastDate).first()
        assertEquals(2, pastTasks.size)
        assertEquals(1, pastTasks.count { it.isCompleted })

        // Complete Task 2 today
        val task2 = taskDao.getTaskById(id2)
        assertNotNull(task2)
        task2?.let { repository.toggleTaskCompletion(it) }

        // Verify updated progress on original date: now 2 of 2 completed (100%)
        pastTasks = repository.getTasksForOriginalDate(pastDate).first()
        assertEquals(2, pastTasks.size)
        assertEquals(2, pastTasks.count { it.isCompleted })
        assertEquals(pastDate, pastTasks.find { it.id == id2 }?.originalDateKey)
    }

    @Test
    fun `theme mode preferences save and load correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("my_day_prefs", Context.MODE_PRIVATE)

        // Default should be SYSTEM
        val defaultMode = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name)
        assertEquals("SYSTEM", defaultMode)

        // Update to DARK
        prefs.edit().putString("app_theme_mode", AppThemeMode.DARK.name).commit()
        val updatedMode = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name)
        assertEquals("DARK", updatedMode)

        // Update to LIGHT
        prefs.edit().putString("app_theme_mode", AppThemeMode.LIGHT.name).commit()
        val lightMode = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name)
        assertEquals("LIGHT", lightMode)
    }

    @Test
    fun `color theme and font size preferences save and load correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("my_day_prefs", Context.MODE_PRIVATE)

        // Test Color Theme
        prefs.edit().putString("app_color_theme", AppColorTheme.ROSE_PINK.name).commit()
        val savedColorTheme = prefs.getString("app_color_theme", AppColorTheme.OCEAN_BLUE.name)
        assertEquals("ROSE_PINK", savedColorTheme)

        // Test Custom Hex Color
        prefs.edit().putString("custom_color_hex", "#9333EA").commit()
        val savedHex = prefs.getString("custom_color_hex", "#2563EB")
        assertEquals("#9333EA", savedHex)

        // Test Font Size
        prefs.edit().putString("app_font_size", AppFontSize.EXTRA_LARGE.name).commit()
        val savedFontSize = prefs.getString("app_font_size", AppFontSize.NORMAL.name)
        assertEquals("EXTRA_LARGE", savedFontSize)
    }
}
