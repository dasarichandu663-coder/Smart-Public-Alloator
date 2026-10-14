package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.navigation.Screen
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicGreenPrimaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicTopAppBar(
    currentScreen: Screen,
    language: Language,
    isAdminMode: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleAdminMode: () -> Unit,
    onNavigateToTestBench: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == Language.TE) "స్మార్ట్ పబ్లిక్ రిసోర్స్ అలోకేటర్" else "Smart Public Allocator",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CivicGreenPrimaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SEVA FIRST INNOVATION",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicGreenPrimary
                        )
                    }
                    if (isAdminMode) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ADMIN MODE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        },
        actions = {
            // Language selector button
            FilledTonalButton(
                onClick = onToggleLanguage,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("language_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Change Language",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == Language.EN) "తెలుగు" else "English",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Test bench quick button
            IconButton(
                onClick = onNavigateToTestBench,
                modifier = Modifier.testTag("test_bench_nav_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Testing Bench & Metrics",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Mode Toggle (Citizen vs Admin)
            IconButton(
                onClick = onToggleAdminMode,
                modifier = Modifier.testTag("admin_mode_toggle_button")
            ) {
                Icon(
                    imageVector = if (isAdminMode) Icons.Default.Person else Icons.Default.AdminPanelSettings,
                    contentDescription = if (isAdminMode) "Switch to Citizen Mode" else "Switch to Admin Mode",
                    tint = if (isAdminMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
