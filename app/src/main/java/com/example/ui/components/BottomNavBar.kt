package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScreenTab

sealed class BottomNavItem(
    val tab: ScreenTab,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Tasks : BottomNavItem(
        ScreenTab.TASKS,
        "Tasks",
        Icons.Filled.Checklist,
        Icons.Outlined.Checklist,
        "nav_tasks"
    )

    object Progress : BottomNavItem(
        ScreenTab.PROGRESS,
        "Progress",
        Icons.Filled.DonutLarge,
        Icons.Outlined.DonutLarge,
        "nav_progress"
    )
}

@Composable
fun BottomNavBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    val items = listOf(
        BottomNavItem.Tasks,
        BottomNavItem.Progress
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(item.tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
