package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.Language
import com.example.ui.navigation.Screen

@Composable
fun CivicBottomNav(
    currentScreen: Screen,
    isAdminMode: Boolean,
    language: Language,
    onNavigate: (Screen) -> Unit
) {
    val items = if (isAdminMode) {
        listOf(
            Screen.AdminDashboard to Icons.Default.AdminPanelSettings,
            Screen.TestBench to Icons.Default.Science,
            Screen.Home to Icons.Default.Home,
            Screen.ServiceFinder to Icons.Default.Search,
            Screen.DocumentChecklist to Icons.Default.Checklist
        )
    } else {
        listOf(
            Screen.Home to Icons.Default.Home,
            Screen.ServiceFinder to Icons.Default.Search,
            Screen.DocumentChecklist to Icons.Default.Checklist,
            Screen.ApplicationGuidance to Icons.Default.Directions,
            Screen.MyRequests to Icons.Default.Assignment
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { (screen, icon) ->
            val label = if (language == Language.TE) screen.labelTe else screen.labelEn
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.testTag("nav_${screen.route}")
            )
        }
    }
}
