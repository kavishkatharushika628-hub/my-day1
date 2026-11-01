package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    allTodayTasks: List<TaskEntity>,
    activeFilter: TaskFilter,
    todayFormatted: String,
    onFilterChange: (TaskFilter) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onOpenAddDialog: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val totalCount = allTodayTasks.size
    val completedCount = allTodayTasks.count { it.isCompleted }
    val remainingCount = totalCount - completedCount
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val todayKey = DateUtils.getTodayKey()

    val carriedOverCount = allTodayTasks.count {
        it.originalDateKey.isNotBlank() && it.originalDateKey < todayKey && !it.isCompleted
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Header: Greeting, Date, and Settings Gear Icon
            item(key = "tasks_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = todayFormatted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "My Day",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (totalCount == 0) {
                                "No tasks yet. Add your first task to plan your day."
                            } else if (remainingCount == 0) {
                                "All tasks completed! Great work today."
                            } else {
                                buildString {
                                    append("$remainingCount ${if (remainingCount == 1) "task" else "tasks"} remaining")
                                    if (carriedOverCount > 0) {
                                        append(" ($carriedOverCount carried forward)")
                                    }
                                }
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Settings Gear Button in Top-Right Corner
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .testTag("settings_gear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Open Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Daily Progress Overview Card (Flat, zero-shadow for optimal GPU rendering)
            item(key = "tasks_progress_card") {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .testTag("progress_overview_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Progress",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$completedCount of $totalCount completed",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Direct progress calculation without animation frame loop
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            // Filter Tabs
            item(key = "tasks_filter_tabs") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        Triple(TaskFilter.ALL, "All", totalCount),
                        Triple(TaskFilter.PENDING, "Pending", remainingCount),
                        Triple(TaskFilter.COMPLETED, "Done", completedCount)
                    )

                    filterOptions.forEach { (filter, label, count) ->
                        val isSelected = activeFilter == filter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onFilterChange(filter) }
                                .padding(vertical = 10.dp)
                                .testTag("filter_${label.lowercase()}")
                        ) {
                            Text(
                                text = "$label ($count)",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Task List or Empty State
            if (tasks.isEmpty()) {
                item(key = "tasks_empty_state") {
                    EmptyTasksView(
                        filter = activeFilter,
                        totalCount = totalCount,
                        onAddClick = onOpenAddDialog
                    )
                }
            } else {
                items(
                    items = tasks,
                    key = { it.id },
                    contentType = { "task_card" }
                ) { task ->
                    val isCarriedOver = task.originalDateKey.isNotBlank() && task.originalDateKey < todayKey
                    TaskCard(
                        task = task,
                        isCarriedOver = isCarriedOver,
                        onToggle = { onToggleTask(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Add Task FAB
        FloatingActionButton(
            onClick = onOpenAddDialog,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(60.dp)
                .testTag("add_task_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    isCarriedOver: Boolean = false,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Button (48dp touch target)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() }
                    .testTag("task_checkbox_${task.id}")
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

            // Task Content
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
                    // Carried Forward badge if originated on an earlier day
                    if (isCarriedOver) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (task.isCompleted) {
                                        "Carried • Done today"
                                    } else {
                                        "Carried from ${DateUtils.formatKeyForShortDisplay(task.originalDateKey)}"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Category chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Priority indicator if High or Medium
                    if (task.priority == "High") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "High",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SoftRed
                            )
                        }
                    } else if (task.priority == "Medium") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Med",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberOrange
                            )
                        }
                    }
                }
            }

            // Delete Action Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("task_delete_button_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyTasksView(
    filter: TaskFilter,
    totalCount: Int,
    onAddClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val (headline, subtitle) = when {
                filter == TaskFilter.COMPLETED -> Pair(
                    "No completed tasks yet",
                    "Complete tasks by clicking the check circle to build daily momentum."
                )
                filter == TaskFilter.PENDING && totalCount > 0 -> Pair(
                    "All done for today!",
                    "You've completed every task on your list. Enjoy your evening!"
                )
                else -> Pair(
                    "No tasks yet. Add your first task!",
                    "Tap the + button below to plan your day."
                )
            }

            Text(
                text = headline,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            if (filter != TaskFilter.COMPLETED) {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("empty_state_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add First Task", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
