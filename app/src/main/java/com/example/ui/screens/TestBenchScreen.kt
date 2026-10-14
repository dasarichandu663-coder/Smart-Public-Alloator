package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BenchmarkedMetric
import com.example.data.model.Language
import com.example.ui.theme.*

@Composable
fun TestBenchScreen(
    language: Language,
    benchmarkMetrics: List<BenchmarkedMetric>,
    onRunScenario: (Int) -> Unit,
    onUpdateMetric: (Int, String, Boolean, String) -> Unit
) {
    var editingMetric by remember { mutableStateOf<BenchmarkedMetric?>(null) }
    var editValue by remember { mutableStateOf("") }
    var editNotes by remember { mutableStateOf("") }
    var editPassed by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.TE) "పరీక్షలు & కొలమానాల బెంచ్" else "Testing & Measurement Bench",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CivicGreenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SEVA Innovation Challenge Empirical Verification: 9 Structured Test Scenarios & Measurement Recording.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 14 Notice
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CivicBlueSecondaryContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicBlueSecondary.copy(alpha = 0.3f)))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Empirical Evaluation Protocol (Section 14)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = CivicBlueSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Undergraduate team members can execute each live scenario and input verified measurements (latency, completion rate, accuracy, satisfaction, and scheduling rule violations).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 9 Structured Test Scenarios List
        item {
            Text(
                text = "9 Required Test Scenarios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(benchmarkMetrics) { metric ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scenario_card_${metric.scenarioNumber}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CivicGreenPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${metric.scenarioNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CivicGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = metric.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (metric.isPassed) StatusVerifiedBg else StatusUrgentBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (metric.isPassed) "VERIFIED PASS" else "FLAGGED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (metric.isPassed) StatusVerifiedGreen else StatusUrgentRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "Metric Target:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = metric.targetGoal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Measured Value:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = metric.measuredValue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CivicGreenPrimary)
                        }
                    }

                    if (metric.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Notes: ${metric.notes}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onRunScenario(metric.scenarioNumber) },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("run_scenario_${metric.scenarioNumber}"),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Live Test", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                editingMetric = metric
                                editValue = metric.measuredValue
                                editNotes = metric.notes
                                editPassed = metric.isPassed
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("edit_metric_${metric.scenarioNumber}"),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Record Result", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    // Edit Result Modal
    if (editingMetric != null) {
        val m = editingMetric!!
        AlertDialog(
            onDismissRequest = { editingMetric = null },
            title = {
                Text(
                    text = "Record Measured Result (Scenario #${m.scenarioNumber})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = m.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        label = { Text("Measured Metric Value") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Observations & Audit Notes") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = editPassed, onCheckedChange = { editPassed = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Test Criteria Passed & Verified", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateMetric(m.scenarioNumber, editValue, editPassed, editNotes)
                        editingMetric = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary)
                ) {
                    Text("Save Measurement")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMetric = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
