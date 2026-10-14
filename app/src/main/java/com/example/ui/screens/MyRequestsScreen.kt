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
import com.example.data.model.DemoAppointment
import com.example.data.model.Language
import com.example.data.model.ReportedIssue
import com.example.data.model.ServiceRecord
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun MyRequestsScreen(
    language: Language,
    appointments: List<DemoAppointment>,
    services: List<ServiceRecord>,
    readyDocuments: Map<String, Set<String>>,
    reportedIssues: List<ReportedIssue>,
    onReportIssue: (String, String, String, String) -> Unit,
    onSelectService: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedServiceForReport by remember { mutableStateOf("Income Certificate (AP)") }
    var issueType by remember { mutableStateOf("Outdated Fee / Link") }
    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val issueTypes = listOf(
        "Outdated Fee / Link",
        "Wrong Responsible Department",
        "Missing Document Requirement",
        "Portal Website Changed",
        "Other Civic Feedback"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.TE) "నా అభ్యర్థనలు & సేవల రికార్డులు" else "My Requests & Records",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CivicGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.TE) {
                            "మీ సేవ్ చేసిన టోకెన్లు, చెక్‌లిస్ట్ పురోగతి మరియు ఫీడ్‌బ్యాక్."
                        } else {
                            "Your saved demo tokens, document readiness status, and reported updates."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Demo Notice
        item {
            DemoNoticeBanner(language = language)
        }

        // Action Button: Report Incorrect Service Information
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.TE) "తప్పుడు సమాచారాన్ని నివేదించండి" else "Report Incorrect Service Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (language == Language.TE) "మారిన లింకులు లేదా ఫీజుల వివరాలను అడ్మిన్‌కు తెలియజేయండి." else "Help keep civic records accurate by reporting broken links or outdated fees.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showReportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicBlueSecondary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("report_issue_button")
                    ) {
                        Icon(Icons.Default.Feedback, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == Language.TE) "నివేదించు" else "Report", fontSize = 11.sp)
                    }
                }
            }
        }

        // Saved Demo Appointments Header
        item {
            Text(
                text = if (language == Language.TE) "సేవ్ చేసిన డెమో టోకెన్లు (${appointments.size})" else "Active Demo Kendra Tokens (${appointments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (appointments.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language == Language.TE) "డెమో టోకెన్లు ఏవీ లేవు." else "No simulated tokens generated yet.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(appointments) { apt ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = apt.tokenNumber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CivicGreenPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StatusUrgentBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(apt.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusUrgentRed)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Service: ${apt.serviceName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "Center: ${apt.centerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Date: ${apt.appointmentDate} • ${apt.slotTime}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Saved Checklists Overview
        item {
            Text(
                text = if (language == Language.TE) "పత్రాల చెక్‌లిస్ట్ పురోగతి" else "Saved Document Checklist Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(services) { s ->
            val readySet = readyDocuments[s.id] ?: emptySet()
            val total = s.requiredDocuments.size
            val ready = s.requiredDocuments.count { readySet.contains(it.id) }
            val pct = if (total > 0) ready.toFloat() / total else 0f

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.TE) s.nameTe else s.nameEn,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$ready / $total Ready",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ready == total) StatusVerifiedGreen else CivicAmberTertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (ready == total) StatusVerifiedGreen else CivicGreenPrimary,
                        trackColor = CivicSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onSelectService(s.id)
                                onNavigate(Screen.DocumentChecklist)
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(if (language == Language.TE) "చెక్‌లిస్ట్ తెరవండి >" else "Open Checklist >", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Reported Issues Log
        if (reportedIssues.isNotEmpty()) {
            item {
                Text(
                    text = if (language == Language.TE) "మీరు సమర్పించిన నివేదికలు" else "Submitted Issue Reports",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(reportedIssues) { issue ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${issue.serviceName} • ${issue.issueType}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = issue.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Logged on ${issue.date} (Awaiting Admin Review)", fontSize = 10.sp, color = CivicBlueSecondary)
                    }
                }
            }
        }
    }

    // Report Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = if (language == Language.TE) "తప్పుడు సమాచారాన్ని నివేదించండి" else "Report Incorrect Service Info",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (language == Language.TE) "సేవ:" else "Service:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = selectedServiceForReport,
                        onValueChange = { selectedServiceForReport = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (language == Language.TE) "సమస్య రకం:" else "Issue Type:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = issueType,
                        onValueChange = { issueType = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (language == Language.TE) "వివరణ:" else "Description:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("e.g. MeeSeva link has updated, or fee changed...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (language == Language.TE) "మీ ఇమెయిల్ (ఐచ్ఛికం):" else "Your Email (Optional):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (description.isNotBlank()) {
                            onReportIssue(selectedServiceForReport, issueType, description, email)
                            showReportDialog = false
                            description = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary)
                ) {
                    Text(if (language == Language.TE) "సమర్పించు" else "Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(if (language == Language.TE) "రద్దు" else "Cancel")
                }
            }
        )
    }
}
