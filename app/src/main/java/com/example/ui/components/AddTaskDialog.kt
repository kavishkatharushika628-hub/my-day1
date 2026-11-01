package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.SoftRed
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var showError by remember { mutableStateOf(false) }

    val categories = listOf("General", "Work", "Study", "Personal", "Health")
    val priorities = listOf("Low", "Medium", "High")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                .testTag("add_task_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                Text(
                    text = "What would you like to achieve?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showError = false
                    },
                    placeholder = {
                        Text(
                            text = "e.g. Review weekly goals",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp
                        )
                    },
                    singleLine = false,
                    maxLines = 3,
                    isError = showError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please enter a task title",
                        color = SoftRed,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Priority Selection
                Text(
                    text = "Priority",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    priorities.forEach { priority ->
                        val isSelected = priority == selectedPriority
                        val (chipBg, chipBorder, chipText) = when (priority) {
                            "High" -> Triple(
                                if (isSelected) SoftRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                if (isSelected) SoftRed else MaterialTheme.colorScheme.outline,
                                if (isSelected) SoftRed else MaterialTheme.colorScheme.onSurface
                            )
                            "Medium" -> Triple(
                                if (isSelected) AmberOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                if (isSelected) AmberOrange else MaterialTheme.colorScheme.outline,
                                if (isSelected) AmberOrange else MaterialTheme.colorScheme.onSurface
                            )
                            else -> Triple(
                                if (isSelected) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                if (isSelected) SuccessGreen else MaterialTheme.colorScheme.outline,
                                if (isSelected) SuccessGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 10.dp)
                                .testTag("priority_chip_$priority")
                        ) {
                            Text(
                                text = priority,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("cancel_add_task_button")
                    ) {
                        Text(text = "Cancel", fontSize = 15.sp)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                showError = true
                            } else {
                                onConfirm(title.trim(), selectedCategory, selectedPriority)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("submit_add_task_button")
                    ) {
                        Text(
                            text = "Add Task",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
