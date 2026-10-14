package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun AdminDashboardScreen(
    language: Language,
    tasks: List<TaskRecord>,
    staff: List<StaffMember>,
    services: List<ServiceRecord>,
    allocationResult: AllocationResult?,
    baselineResult: AllocationResult?,
    onRunClassicalAllocation: () -> Unit,
    onRunBaselineAllocation: () -> Unit,
    onApproveAllocation: () -> Unit,
    onToggleServiceVerification: (String) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<ServiceCategory?>(null) }
    var selectedPriorityFilter by remember { mutableStateOf<TaskPriority?>(null) }
    var showOnlyUnassigned by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) } // 0: Allocation Engine, 1: 50 Tasks Table, 2: Knowledge Base Manager

    val totalTasks = tasks.size
    val pendingCount = tasks.count { it.status == TaskStatus.PENDING }
    val assignedCount = tasks.count { it.status == TaskStatus.ASSIGNED }
    val availableStaffCount = staff.count { it.isAvailable }

    val filteredTasks = tasks.filter { task ->
        (selectedCategoryFilter == null || task.category == selectedCategoryFilter) &&
                (selectedPriorityFilter == null || task.priority == selectedPriorityFilter) &&
                (!showOnlyUnassigned || task.status == TaskStatus.PENDING)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Title & Role Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.TE) "అడ్మినిస్ట్రేటర్ డాష్‌బోర్డ్" else "Administrator Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CivicGreenPrimary
                    )
                    Text(
                        text = "Public Service Operations & Staff Workload Allocation",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SUPERVISOR ROLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Demo Notice
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CivicBlueSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIMULATED HACKATHON DATASET: 50 citizen tasks & 5 public service staff members.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CivicBlueSecondary
                    )
                }
            }
        }

        // Key Metrics Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(title = "Total Tasks", value = "$totalTasks", icon = Icons.Default.ListAlt, modifier = Modifier.weight(1f))
                MetricCard(title = "Assigned", value = "$assignedCount", icon = Icons.Default.CheckCircle, color = StatusVerifiedGreen, modifier = Modifier.weight(1f))
                MetricCard(title = "Pending", value = "$pendingCount", icon = Icons.Default.PendingActions, color = CivicAmberTertiary, modifier = Modifier.weight(1f))
                MetricCard(title = "Staff Active", value = "$availableStaffCount/5", icon = Icons.Default.People, modifier = Modifier.weight(1f))
            }
        }

        // Navigation Tabs (Allocation Engine, Tasks Table, Knowledge Base)
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CivicGreenPrimary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Allocation Engine", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_allocation_engine")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("50 Tasks List ($totalTasks)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_tasks_list")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Knowledge Base (${services.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_knowledge_base")
                )
            }
        }

        // ==========================================
        // TAB 0: CLASSICAL WORKLOAD ALLOCATION ENGINE (SECTION 11)
        // ==========================================
        if (activeTab == 0) {
            // Classical Algorithm Disclaimer & Rule Verification
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicGreenPrimaryContainer.copy(alpha = 0.5f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicGreenPrimary.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Classical Rule-Based Scheduling Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CivicGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Transparent deterministic greedy heuristic: checks staff department eligibility, availability, respects 8h capacity constraint, and prioritizes urgent tasks. Ordinary classical code only (Zero quantum claims).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Trigger Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onRunClassicalAllocation,
                                colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("run_classical_allocation_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run SEVA Balanced Allocation", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = onRunBaselineAllocation,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("run_baseline_allocation_button")
                            ) {
                                Text("Compare Baseline", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Comparative Allocation Statistics
            if (allocationResult != null) {
                item {
                    val res = allocationResult
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = res.algorithmName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CivicGreenPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (res.ruleViolationsCount == 0) StatusVerifiedBg else StatusUrgentBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${res.ruleViolationsCount} Rule Violations",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.ruleViolationsCount == 0) StatusVerifiedGreen else StatusUrgentRed
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            // Statistical Comparison Table
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Metric", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Workload Std Dev", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("Max-Min Spread", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("Assigned Tasks", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("Unassigned Tasks", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("Execution Latency", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Before Allocation", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${res.stdDevBefore} hrs", fontSize = 11.sp)
                                    Text("${res.maxMinSpreadBefore} hrs", fontSize = 11.sp)
                                    Text("0 / 50", fontSize = 11.sp)
                                    Text("50", fontSize = 11.sp)
                                    Text("-", fontSize = 11.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("After Allocation", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CivicGreenPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${res.stdDevAfter} hrs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusVerifiedGreen)
                                    Text("${res.maxMinSpreadAfter} hrs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("${res.assignedTasks.size} / 50", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("${res.unassignedTasks.size}", fontSize = 11.sp)
                                    Text("${res.executionTimeMs} ms", fontSize = 11.sp)
                                }
                            }

                            // Baseline Comparison Callout if present
                            if (baselineResult != null) {
                                val b = baselineResult
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Naive Baseline Violations: ${b.ruleViolationsCount}", fontSize = 11.sp, color = StatusUrgentRed, fontWeight = FontWeight.Bold)
                                        Text(text = "SEVA Greedy Violations: ${res.ruleViolationsCount}", fontSize = 11.sp, color = StatusVerifiedGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mandatory Approval Action: Administrator must review & approve
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (res.isApprovedByAdmin) "Approved by Administrator" else "Pending Administrator Review",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.isApprovedByAdmin) StatusVerifiedGreen else CivicAmberTertiary
                                )
                                Button(
                                    onClick = onApproveAllocation,
                                    enabled = !res.isApprovedByAdmin,
                                    colors = ButtonDefaults.buttonColors(containerColor = CivicBlueSecondary),
                                    modifier = Modifier.testTag("approve_allocation_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (res.isApprovedByAdmin) "Approved" else "Review & Approve", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Staff Capacity & Workload Cards
            item {
                Text(
                    text = "Staff Workload Distribution (5 Team Members)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(staff) { s ->
                val assignedToStaff = tasks.filter { it.assignedStaffId == s.id }
                val loadRatio = (s.currentWorkloadHours / s.maxDailyCapacityHours).coerceIn(0.0, 1.0).toFloat()
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
                            Column {
                                Text(text = s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = s.designation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = "${(s.currentWorkloadHours * 10).roundToInt() / 10.0}h / ${s.maxDailyCapacityHours}h",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (s.currentWorkloadHours > s.maxDailyCapacityHours) StatusUrgentRed else CivicGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Eligible Depts: ${s.eligibleCategories.joinToString { it.displayNameEn.split(" ").first() }}",
                            fontSize = 10.sp,
                            color = CivicBlueSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { loadRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (s.currentWorkloadHours > s.maxDailyCapacityHours) StatusUrgentRed else CivicGreenPrimary,
                            trackColor = CivicSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${assignedToStaff.size} Tasks Assigned • ${if (s.isAvailable) "Available On Duty" else "On Leave"}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Transparent Justification Audit Trail
            item {
                Text(
                    text = "Recommended Assignment Audit Trail (Explanations)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(tasks.take(10)) { t ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${t.id}: ${t.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            PriorityBadge(priority = t.priority)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Rationale: ${t.assignmentReason ?: "Pending evaluation"}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: 50 TASKS TABLE & FILTER
        // ==========================================
        if (activeTab == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Filter Tasks:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedCategoryFilter == null,
                                    onClick = { selectedCategoryFilter = null },
                                    label = { Text("All Depts", fontSize = 10.sp) }
                                )
                            }
                            items(ServiceCategory.values().toList()) { cat ->
                                FilterChip(
                                    selected = selectedCategoryFilter == cat,
                                    onClick = { selectedCategoryFilter = cat },
                                    label = { Text(cat.displayNameEn.split(" ").first(), fontSize = 10.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedPriorityFilter == null,
                                    onClick = { selectedPriorityFilter = null },
                                    label = { Text("All Priorities", fontSize = 10.sp) }
                                )
                            }
                            items(TaskPriority.values().toList()) { p ->
                                FilterChip(
                                    selected = selectedPriorityFilter == p,
                                    onClick = { selectedPriorityFilter = p },
                                    label = { Text(p.displayName, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }

            items(filteredTasks) { task ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = task.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CivicBlueSecondary)
                                PriorityBadge(priority = task.priority)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = task.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Citizen: ${task.citizenName} • ${task.effortHours}h effort", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (task.assignedStaffName != null) {
                                Text(text = "Assigned to: ${task.assignedStaffName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusVerifiedGreen)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: SERVICE KNOWLEDGE BASE MANAGER
        // ==========================================
        if (activeTab == 2) {
            item {
                Text(
                    text = "Service Knowledge Base Manager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(services) { serv ->
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
                            Text(text = serv.nameEn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            VerificationBadge(isVerified = serv.isVerified, lastVerifiedDate = serv.lastVerifiedDate)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Dept: ${serv.departmentEn}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Official URL: ${serv.officialPortalUrl}", fontSize = 10.sp, color = CivicBlueSecondary)
                        Text(text = "Source: ${serv.officialVerificationSource}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilledTonalButton(
                                onClick = { onToggleServiceVerification(serv.id) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (serv.isVerified) "Mark as Demo" else "Verify Source", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = CivicGreenPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
