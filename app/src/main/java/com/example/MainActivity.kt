package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()
            val customColorHex by viewModel.customColorHex.collectAsStateWithLifecycle()
            val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = themeMode,
                colorTheme = colorTheme,
                customColorHex = customColorHex,
                fontSize = fontSize
            ) {
                MyDayApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MyDayApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handled gracefully
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()
    val customColorHex by viewModel.customColorHex.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val filteredTodayTasks by viewModel.filteredTodayTasks.collectAsStateWithLifecycle()
    val activeFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val isAddTaskDialogVisible by viewModel.isAddTaskDialogVisible.collectAsStateWithLifecycle()

    val selectedProgressDateKey by viewModel.selectedProgressDateKey.collectAsStateWithLifecycle()
    val availableDateKeys by viewModel.availableDateKeys.collectAsStateWithLifecycle()
    val progressDateFilter by viewModel.progressDateFilter.collectAsStateWithLifecycle()
    val tasksForSelectedProgressDate by viewModel.tasksForSelectedProgressDate.collectAsStateWithLifecycle()
    val filteredTasksForSelectedProgressDate by viewModel.filteredTasksForSelectedProgressDate.collectAsStateWithLifecycle()

    // Back button handling
    BackHandler(enabled = isSettingsOpen || currentTab != ScreenTab.TASKS) {
        if (isSettingsOpen) {
            viewModel.closeSettings()
        } else {
            viewModel.selectTab(ScreenTab.TASKS)
        }
    }

    if (isSettingsOpen) {
        SettingsScreen(
            currentTheme = themeMode,
            currentColorTheme = colorTheme,
            customColorHex = customColorHex,
            currentFontSize = fontSize,
            onThemeSelected = { newMode -> viewModel.setThemeMode(newMode) },
            onColorThemeSelected = { newColorTheme -> viewModel.setColorTheme(newColorTheme) },
            onCustomColorConfirmed = { hex -> viewModel.setCustomColor(hex) },
            onFontSizeSelected = { newSize -> viewModel.setFontSize(newSize) },
            onBack = { viewModel.closeSettings() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ScreenTab.TASKS -> {
                        TasksScreen(
                            tasks = filteredTodayTasks,
                            allTodayTasks = todayTasks,
                            activeFilter = activeFilter,
                            todayFormatted = viewModel.getTodayFormatted(),
                            onFilterChange = { filter -> viewModel.setFilter(filter) },
                            onToggleTask = { task -> viewModel.toggleTask(task) },
                            onDeleteTask = { task -> viewModel.deleteTask(task) },
                            onOpenAddDialog = { viewModel.showAddTaskDialog(true) },
                            onOpenSettings = { viewModel.openSettings() }
                        )
                    }

                    ScreenTab.PROGRESS -> {
                        ProgressScreen(
                            selectedDateKey = selectedProgressDateKey,
                            availableDateKeys = availableDateKeys,
                            allTasks = allTasks,
                            dateFilter = progressDateFilter,
                            tasksForSelectedDate = tasksForSelectedProgressDate,
                            filteredTasksForSelectedDate = filteredTasksForSelectedProgressDate,
                            onSelectDate = { dateKey -> viewModel.selectProgressDate(dateKey) },
                            onPreviousDay = { viewModel.goToPreviousProgressDate() },
                            onNextDay = { viewModel.goToNextProgressDate() },
                            onResetToToday = { viewModel.resetProgressToToday() },
                            onFilterChange = { filter -> viewModel.setProgressDateFilter(filter) },
                            onToggleTask = { task -> viewModel.toggleTask(task) },
                            onDeleteTask = { task -> viewModel.deleteTask(task) },
                            onClearCompletedForDate = { dateKey -> viewModel.clearCompletedTasksForDate(dateKey) }
                        )
                    }
                }
            }
        }
    }

    if (isAddTaskDialogVisible) {
        AddTaskDialog(
            onDismiss = { viewModel.showAddTaskDialog(false) },
            onConfirm = { title, category, priority ->
                viewModel.addTask(title, category, priority)
            }
        )
    }
}
