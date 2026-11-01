package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.TaskFilter
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.SoftRed
import com.example.ui.theme.SuccessGreen
import com.example.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    selectedDateKey: String,
    availableDateKeys: List<String>,
    allTasks: List<TaskEntity>,
    dateFilter: TaskFilter,
    tasksForSelectedDate: List<TaskEntity>,
    filteredTasksForSelectedDate: List<TaskEntity>,
    onSelectDate: (String) -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToToday: () -> Unit,
    onFilterChange: (TaskFilter) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onClearCompletedForDate: (String) -> Unit
) {
    val todayKey = DateUtils.getTodayKey()
    val isViewingToday = selectedDateKey == todayKey

    val totalTasksForDate = tasksForSelectedDate.size
    val completedTasksForDate = tasksForSelectedDate.filter { it.isCompleted }
    val pendingTasksForDate = tasksForSelectedDate.filter { !it.isCompleted }

    val completedCount = completedTasksForDate.size
    val remainingCount = pendingTasksForDate.size
    val completionFraction = if (totalTasksForDate > 0) completedCount.toFloat() / totalTasksForDate else 0f

    var showCalendarPicker by remember { mutableStateOf(false) }

    // DatePicker for Search / Calendar Selection
    if (showCalendarPicker) {
        val parsedDate = DateUtils.parseKey(selectedDateKey)
        val initialMillis = parsedDate?.time ?: System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showCalendarPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                            cal.timeInMillis = utcMillis
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            onSelectDate(sdf.format(cal.time))
                        }
                        showCalendarPicker = false
                    },
                    modifier = Modifier.testTag("confirm_date_picker_button")
                ) {
                    Text("Select", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCalendarPicker = false },
                    modifier = Modifier.testTag("cancel_date_picker_button")
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Top Header
        item(key = "progress_header") {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "DAILY PROGRESS & HISTORY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Daily Progress",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Browse historical dates to track consistency and complete pending tasks.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Calendar & Date Navigation Bar
        item(key = "progress_nav_bar") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .testTag("date_navigation_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousDay,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("prev_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showCalendarPicker = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("current_date_display")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateUtils.formatKeyForShortDisplay(selectedDateKey),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = DateUtils.formatKeyForHeader(selectedDateKey),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isViewingToday) {
                            IconButton(
                                onClick = onResetToToday,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("jump_to_today_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "Jump to Today",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onNextDay,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("next_date_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Day",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Recent Dates Strip
        item(key = "progress_date_strip") {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .clickable { showCalendarPicker = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("open_calendar_picker_chip")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Search",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                availableDateKeys.forEach { dateKey ->
                    val isSelected = dateKey == selectedDateKey
                    val tasksForDay = allTasks.filter {
                        val key = if (it.originalDateKey.isBlank()) DateUtils.formatKeyFromTimestamp(it.createdAt) else it.originalDateKey
                        key == dateKey
                    }
                    val total = tasksForDay.size
                    val done = tasksForDay.count { it.isCompleted }
                    val dayDotColor = when {
                        total == 0 -> Color.Transparent
                        done == total -> SuccessGreen
                        done > 0 -> AmberOrange
                        else -> SoftRed
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectDate(dateKey) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("date_chip_$dateKey")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateUtils.getDayOfWeekShort(dateKey),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = DateUtils.getDayOfMonthNumber(dateKey),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )

                            if (total > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else dayDotColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Progress Ring for Selected Date
        item(key = "progress_ring_card") {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .testTag("progress_hero_card")
            ) {
                val ringTrackColor = MaterialTheme.colorScheme.surfaceVariant
                val ringPrimaryColor = MaterialTheme.colorScheme.primary

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(150.dp)
                    ) {
                        // Direct canvas draw without animation ticker frame loop
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = 12.dp.toPx()
                            drawCircle(
                                color = ringTrackColor,
                                style = Stroke(width = stroke)
                            )
                            drawArc(
                                color = if (completionFraction >= 1f && totalTasksForDate > 0) SuccessGreen else ringPrimaryColor,
                                startAngle = -90f,
                                sweepAngle = 360f * completionFraction,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(completionFraction * 100).toInt()}%",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "COMPLETE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val statusMessage = when {
                        totalTasksForDate == 0 -> "No tasks logged for this day."
                        completionFraction >= 1f -> "All tasks completed! 100% finished 🎉"
                        completionFraction >= 0.5f -> "Great progress! Over halfway completed."
                        else -> "$remainingCount tasks still pending."
                    }

                    Text(
                        text = statusMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (totalTasksForDate > 0) {
                            "$completedCount completed • $remainingCount pending"
                        } else {
                            "Select another date to view history."
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!isViewingToday && remainingCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Incomplete tasks carry forward to Today. Complete them here or on Today to update this day's progress.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Metrics Breakdown Row
        item(key = "progress_metrics_row") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$completedCount",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Done",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberOrange.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PendingActions,
                                contentDescription = null,
                                tint = AmberOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$remainingCount",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pending",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalTasksForDate",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Tasks from this Date
        item(key = "progress_section_title") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasks for ${DateUtils.formatKeyForShortDisplay(selectedDateKey)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (completedTasksForDate.isNotEmpty()) {
                    Text(
                        text = "Clear completed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SoftRed,
                        modifier = Modifier
                            .clickable { onClearCompletedForDate(selectedDateKey) }
                            .padding(4.dp)
                            .testTag("clear_completed_history_button")
                    )
                }
            }
        }

        // Filter Pills for this Date
        if (totalTasksForDate > 0) {
            item(key = "progress_filter_pills") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        Triple(TaskFilter.ALL, "All", totalTasksForDate),
                        Triple(TaskFilter.PENDING, "Pending", remainingCount),
                        Triple(TaskFilter.COMPLETED, "Done", completedCount)
                    )

                    filterOptions.forEach { (filter, label, count) ->
                        val isSelected = dateFilter == filter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onFilterChange(filter) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "$label ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Tasks List for Date
        if (tasksForSelectedDate.isEmpty()) {
            item(key = "progress_empty_state") {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        .padding(bottom = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tasks recorded for this day",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use the date strip or calendar to view other days, or jump back to today.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        if (!isViewingToday) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onResetToToday,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Return to Today")
                            }
                        }
                    }
                }
            }
        } else if (filteredTasksForSelectedDate.isEmpty()) {
            item(key = "progress_filter_empty") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(bottom = 20.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "No tasks match the active filter.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(
                items = filteredTasksForSelectedDate,
                key = { it.id },
                contentType = { "historical_task" }
            ) { task ->
                HistoricalTaskCard(
                    task = task,
                    selectedDateKey = selectedDateKey,
                    todayKey = todayKey,
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HistoricalTaskCard(
    task: TaskEntity,
    selectedDateKey: String,
    todayKey: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .testTag("historical_task_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() }
                    .testTag("history_checkbox_${task.id}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) SuccessGreen else MaterialTheme.colorScheme.surface)
                        .border(
                            width = 2.dp,
                            color = if (task.isCompleted) SuccessGreen else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle() }
            ) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (task.isCompleted && task.completedDateKey != null && task.completedDateKey != task.originalDateKey) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Done on ${DateUtils.formatKeyForShortDisplay(task.completedDateKey)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessGreen
                            )
                        }
                    } else if (!task.isCompleted && selectedDateKey < todayKey) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = null,
                                    tint = AmberOrange,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Carried to Today",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberOrange
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("history_delete_button_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
