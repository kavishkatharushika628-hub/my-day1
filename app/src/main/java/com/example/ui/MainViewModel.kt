package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.AppFontSize
import com.example.ui.theme.AppThemeMode
import com.example.util.DateUtils
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    TASKS,
    PROGRESS
}

enum class TaskFilter {
    ALL,
    PENDING,
    COMPLETED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val notificationHelper: NotificationHelper = NotificationHelper(application)
    private val prefs = application.getSharedPreferences("my_day_prefs", Context.MODE_PRIVATE)

    // Screen Navigation
    private val _currentTab = MutableStateFlow(ScreenTab.TASKS)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Display & Customization Preferences
    private val _themeMode = MutableStateFlow(loadThemePreference())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _colorTheme = MutableStateFlow(loadColorThemePreference())
    val colorTheme: StateFlow<AppColorTheme> = _colorTheme.asStateFlow()

    private val _customColorHex = MutableStateFlow(loadCustomColorHexPreference())
    val customColorHex: StateFlow<String> = _customColorHex.asStateFlow()

    private val _fontSize = MutableStateFlow(loadFontSizePreference())
    val fontSize: StateFlow<AppFontSize> = _fontSize.asStateFlow()

    // Task Filter on Tasks Screen
    private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
    val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

    // Selected Date for Progress Screen (defaults to Today)
    private val _selectedProgressDateKey = MutableStateFlow(DateUtils.getTodayKey())
    val selectedProgressDateKey: StateFlow<String> = _selectedProgressDateKey.asStateFlow()

    // Filter on Progress Screen for historical date
    private val _progressDateFilter = MutableStateFlow(TaskFilter.ALL)
    val progressDateFilter: StateFlow<TaskFilter> = _progressDateFilter.asStateFlow()

    // Dialog state for adding tasks
    private val _isAddTaskDialogVisible = MutableStateFlow(false)
    val isAddTaskDialogVisible: StateFlow<Boolean> = _isAddTaskDialogVisible.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())

        // Purge any sample or demo tasks from previous preview builds
        viewModelScope.launch(Dispatchers.IO) {
            val hasPurgedSamples = prefs.getBoolean("has_purged_sample_tasks_v2", false)
            if (!hasPurgedSamples) {
                prefs.edit().putBoolean("has_purged_sample_tasks_v2", true).apply()
                val sampleTitles = listOf(
                    "Plan top 3 priorities for today",
                    "Read a chapter or take a walk",
                    "Hydration & posture stretch",
                    "Complete a 25-minute focus session",
                    "Afternoon stretch & hydration break"
                )
                repository.deleteSampleTasks(sampleTitles)
            }
        }
    }

    private fun loadThemePreference(): AppThemeMode {
        val saved = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(saved ?: AppThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    private fun loadColorThemePreference(): AppColorTheme {
        val saved = prefs.getString("app_color_theme", AppColorTheme.OCEAN_BLUE.name)
        return AppColorTheme.fromName(saved ?: AppColorTheme.OCEAN_BLUE.name)
    }

    private fun loadCustomColorHexPreference(): String {
        return prefs.getString("custom_color_hex", "#2563EB") ?: "#2563EB"
    }

    private fun loadFontSizePreference(): AppFontSize {
        val saved = prefs.getString("app_font_size", AppFontSize.NORMAL.name)
        return AppFontSize.fromName(saved ?: AppFontSize.NORMAL.name)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("app_theme_mode", mode.name).apply()
        }
    }

    fun setColorTheme(theme: AppColorTheme) {
        _colorTheme.value = theme
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("app_color_theme", theme.name).apply()
        }
    }

    fun setCustomColor(hexColor: String) {
        val cleanHex = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
        _customColorHex.value = cleanHex
        _colorTheme.value = AppColorTheme.CUSTOM
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit()
                .putString("custom_color_hex", cleanHex)
                .putString("app_color_theme", AppColorTheme.CUSTOM.name)
                .apply()
        }
    }

    fun setFontSize(size: AppFontSize) {
        _fontSize.value = size
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("app_font_size", size.name).apply()
        }
    }

    fun selectTab(tab: ScreenTab) {
        if (_currentTab.value != tab || _isSettingsOpen.value) {
            _isSettingsOpen.value = false
            _currentTab.value = tab
        }
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun setFilter(filter: TaskFilter) {
        _taskFilter.value = filter
    }

    fun setProgressDateFilter(filter: TaskFilter) {
        _progressDateFilter.value = filter
    }

    fun selectProgressDate(dateKey: String) {
        _selectedProgressDateKey.value = dateKey
    }

    fun goToPreviousProgressDate() {
        _selectedProgressDateKey.value = DateUtils.getPreviousDateKey(_selectedProgressDateKey.value)
    }

    fun goToNextProgressDate() {
        val nextKey = DateUtils.getNextDateKey(_selectedProgressDateKey.value)
        _selectedProgressDateKey.value = nextKey
    }

    fun resetProgressToToday() {
        _selectedProgressDateKey.value = DateUtils.getTodayKey()
    }

    fun showAddTaskDialog(show: Boolean) {
        _isAddTaskDialogVisible.value = show
    }

    // All Tasks from Database - cached in StateFlow with WhileSubscribed(5000)
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All dates recorded in DB
    val recordedDateKeys: StateFlow<List<String>> = repository.allRecordedDateKeys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Available dates for horizontal selector (combines recent 14 days + recorded dates)
    val availableDateKeys: StateFlow<List<String>> = combine(recordedDateKeys, _selectedProgressDateKey) { recorded, selected ->
        val recent = DateUtils.getRecentDays(14)
        val combined = (recent + recorded + listOf(selected)).distinct().sortedDescending()
        combined
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DateUtils.getRecentDays(14))

    /**
     * Today's Active Tasks list:
     * Automatically carries forward incomplete tasks from previous days to today.
     * Zero duplicate task entities are created.
     */
    val todayTasks: StateFlow<List<TaskEntity>> = allTasks.map { tasks ->
        val todayKey = DateUtils.getTodayKey()
        tasks.filter { task ->
            val taskOriginalKey = if (task.originalDateKey.isBlank()) {
                DateUtils.formatKeyFromTimestamp(task.createdAt)
            } else {
                task.originalDateKey
            }

            val isFromToday = taskOriginalKey == todayKey
            val isCarriedForwardPending = !task.isCompleted && taskOriginalKey < todayKey
            val isCompletedToday = task.isCompleted && task.completedDateKey == todayKey

            isFromToday || isCarriedForwardPending || isCompletedToday
        }.sortedWith(compareBy({ it.isCompleted }, { -it.createdAt }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTodayTasks: StateFlow<List<TaskEntity>> = combine(todayTasks, _taskFilter) { tasks, filter ->
        when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Tasks for Selected Date in Progress Screen:
     * Displays all tasks whose original date is the selected date.
     */
    val tasksForSelectedProgressDate: StateFlow<List<TaskEntity>> = combine(allTasks, _selectedProgressDateKey) { tasks, selectedDate ->
        tasks.filter { task ->
            val taskOriginalKey = if (task.originalDateKey.isBlank()) {
                DateUtils.formatKeyFromTimestamp(task.createdAt)
            } else {
                task.originalDateKey
            }
            taskOriginalKey == selectedDate
        }.sortedWith(compareBy({ it.isCompleted }, { -it.createdAt }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasksForSelectedProgressDate: StateFlow<List<TaskEntity>> = combine(
        tasksForSelectedProgressDate,
        _progressDateFilter
    ) { tasks, filter ->
        when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Task Operations
    fun addTask(
        title: String,
        category: String = "General",
        priority: String = "Medium",
        dateKey: String = DateUtils.getTodayKey()
    ) {
        if (title.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(
                TaskEntity(
                    title = title.trim(),
                    category = category,
                    priority = priority,
                    originalDateKey = dateKey,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val willBeCompleted = !task.isCompleted
            repository.toggleTaskCompletion(task)

            if (willBeCompleted) {
                val currentTodayTasks = todayTasks.value
                val remainingCount = currentTodayTasks.count { it.id != task.id && !it.isCompleted }
                if (remainingCount == 0 && currentTodayTasks.isNotEmpty()) {
                    notificationHelper.notifyAllTasksCompleted()
                }
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun clearCompletedTasksForToday() {
        viewModelScope.launch(Dispatchers.IO) {
            val completedToday = todayTasks.value.filter { it.isCompleted }
            completedToday.forEach { repository.deleteTask(it) }
        }
    }

    fun clearCompletedTasksForDate(dateKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompletedTasksForDate(dateKey)
        }
    }

    fun getTodayFormatted(): String {
        return DateUtils.formatKeyForHeader(DateUtils.getTodayKey())
    }
}
