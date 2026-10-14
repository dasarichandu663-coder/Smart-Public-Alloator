package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.ServiceRecord
import com.example.ui.components.CivicWarningBanner
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun DocumentChecklistScreen(
    language: Language,
    services: List<ServiceRecord>,
    selectedServiceId: String,
    readyDocuments: Map<String, Set<String>>,
    onSelectService: (String) -> Unit,
    onToggleDocumentReady: (String, String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val currentService = services.find { it.id == selectedServiceId } ?: services.firstOrNull()

    if (currentService == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No service selected.")
        }
        return
    }

    val readySet = readyDocuments[currentService.id] ?: emptySet()
    val totalCount = currentService.requiredDocuments.size
    val readyCount = currentService.requiredDocuments.count { readySet.contains(it.id) }
    val progress = if (totalCount > 0) readyCount.toFloat() / totalCount else 0f
    val remainingDocs = currentService.requiredDocuments.filter { !readySet.contains(it.id) }

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
                Text(
                    text = if (language == Language.TE) "పత్రాల సంసిద్ధత చెక్‌లిస్ట్" else "Document Readiness Checklist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CivicGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.TE) {
                        "మీసేవ లేదా ఆన్‌లైన్‌లో దరఖాస్తు చేయడానికి ముందు అవసరమైన ధృవీకరణ పత్రాలను సరిచూసుకోండి."
                    } else {
                        "Review verified document prerequisites before submitting your application at MeeSeva or online."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Service Selector Chips
        item {
            Column {
                Text(
                    text = if (language == Language.TE) "సేవను ఎంచుకోండి:" else "Select Service:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(services) { service ->
                        val isSelected = service.id == currentService.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectService(service.id) },
                            label = {
                                Text(
                                    text = if (language == Language.TE) service.nameTe else service.nameEn,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.testTag("checklist_service_chip_${service.id}")
                        )
                    }
                }
            }
        }

        // Service Summary & Verification Badge
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.TE) currentService.nameTe else currentService.nameEn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentService.departmentEn} • ${currentService.issuingAuthorityEn}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        VerificationBadge(
                            isVerified = currentService.isVerified,
                            lastVerifiedDate = currentService.lastVerifiedDate
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.TE) {
                                "మీరు $totalCount లో $readyCount పత్రాలు సిద్ధంగా ఉన్నట్లు గుర్తించారు."
                            } else {
                                "You have marked $readyCount of $totalCount items as ready."
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (readyCount == totalCount) StatusVerifiedGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CivicGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (readyCount == totalCount) StatusVerifiedGreen else CivicGreenPrimary,
                        trackColor = CivicSurfaceVariant
                    )
                }
            }
        }

        // Privacy Banner (Section 7 constraint: No document upload required)
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.TE) {
                            "గోప్యతా హామీ: ఈ ప్రొటోటైప్‌లో మీ ఆధార్ లేదా సున్నితమైన పత్రాలను అప్‌లోడ్ చేయవలసిన అవసరం లేదు."
                        } else {
                            "Zero Privacy Risk: No sensitive identity or financial records are uploaded or stored for this prototype."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Checklist Items
        item {
            Text(
                text = if (language == Language.TE) "అవసరమైన పత్రాల వివరాలు" else "Required Documents List",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(currentService.requiredDocuments) { doc ->
            val isReady = readySet.contains(doc.id)
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isReady) StatusVerifiedBg.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isReady) StatusVerifiedGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleDocumentReady(currentService.id, doc.id) }
                    .testTag("doc_item_${doc.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = isReady,
                        onCheckedChange = { onToggleDocumentReady(currentService.id, doc.id) },
                        colors = CheckboxDefaults.colors(checkedColor = StatusVerifiedGreen),
                        modifier = Modifier.testTag("checkbox_${doc.id}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.TE) doc.nameTe else doc.nameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (doc.isMandatory) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(StatusUrgentBg)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("Mandatory", fontSize = 9.sp, color = StatusUrgentRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (language == Language.TE) doc.explanationTe else doc.explanationEn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == Language.TE) "ఎక్కడ పొందాలి: " else "Where to obtain: ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicBlueSecondary
                            )
                            Text(
                                text = if (language == Language.TE) doc.whereToObtainTe else doc.whereToObtainEn,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (doc.sourceUrl != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Official Portal Link",
                                fontSize = 10.sp,
                                color = CivicBlueSecondary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.sourceUrl))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isReady) StatusVerifiedBg else CivicAmberContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isReady) (if (language == Language.TE) "సిద్ధం" else "Ready")
                            else (if (language == Language.TE) "కావాలి" else "Missing"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isReady) StatusVerifiedGreen else CivicAmberTertiary
                        )
                    }
                }
            }
        }

        // Remaining Missing Documents Summary Card
        item {
            if (remainingDocs.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicAmberContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CivicAmberTertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.TE) "ఇంకా సిద్ధం కావాల్సిన పత్రాలు (${remainingDocs.size}):"
                                else "Remaining Pending Documents (${remainingDocs.size}):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CivicAmberTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        remainingDocs.forEach { doc ->
                            Text(
                                text = "• " + (if (language == Language.TE) doc.nameTe else doc.nameEn) + " — " + (if (language == Language.TE) doc.whereToObtainTe else doc.whereToObtainEn),
                                fontSize = 11.sp,
                                color = CivicAmberOnContainer
                            )
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusVerifiedBg)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusVerifiedGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == Language.TE) "అన్ని పత్రాలు సంసిద్ధంగా ఉన్నాయి!" else "All Documents Ready!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StatusVerifiedGreen
                            )
                            Text(
                                text = if (language == Language.TE) "మీరు ఇప్పుడు అధికారిక పోర్టల్‌లో దరఖాస్తు చేయవచ్చు." else "You are ready to proceed with official submission.",
                                fontSize = 11.sp,
                                color = StatusVerifiedGreen
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onNavigate(Screen.ApplicationGuidance) },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == Language.TE) "తదుపరి మార్గదర్శకత్వం" else "Next Steps Guide", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentService.officialPortalUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (language == Language.TE) "పోర్టల్ తెరవండి" else "Open Portal", fontSize = 12.sp)
                }
            }
        }

        item {
            CivicWarningBanner(language = language)
        }
    }
}
