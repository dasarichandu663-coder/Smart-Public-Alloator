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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.viewmodel.AiSearchUiState

@Composable
fun ServiceFinderScreen(
    language: Language,
    services: List<ServiceRecord>,
    searchQuery: String,
    selectedState: String,
    aiSearchState: AiSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onExecuteSearch: (String) -> Unit,
    onSelectService: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val states = listOf("Andhra Pradesh", "Telangana", "All India")

    // Filter services locally for instant browsing
    val filteredServices = remember(services, searchQuery, selectedState) {
        if (searchQuery.isBlank()) {
            services.filter { selectedState == "All India" || it.state.equals(selectedState, ignoreCase = true) }
        } else {
            val q = searchQuery.trim().lowercase()
            services.filter { s ->
                val stateMatch = selectedState == "All India" || s.state.equals(selectedState, ignoreCase = true)
                val textMatch = s.nameEn.lowercase().contains(q) ||
                        s.nameTe.lowercase().contains(q) ||
                        s.departmentEn.lowercase().contains(q) ||
                        s.departmentTe.lowercase().contains(q) ||
                        s.altNames.any { it.lowercase().contains(q) }
                stateMatch && textMatch
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Section Header
        item {
            Column {
                Text(
                    text = if (language == Language.TE) "ఏఐ ఆధారిత ప్రభుత్వ సేవల శోధన" else "AI Government Service Finder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CivicGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.TE) {
                        "సహజ భాషలో మీ ప్రశ్నను అడగండి. అధికారిక శాఖ, అర్హతలు మరియు ప్రభుత్వ పోర్టల్ లింకులను తెలుసుకోండి."
                    } else {
                        "Ask any question in plain English or Telugu. Find verified departments, portals, and requirements."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Input & State Selector
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // State chips
                    Text(
                        text = if (language == Language.TE) "రాష్ట్రం / ప్రాంతం:" else "State / Region:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(states) { st ->
                            FilterChip(
                                selected = selectedState == st,
                                onClick = { onStateChange(st) },
                                label = { Text(st, fontSize = 11.sp) },
                                modifier = Modifier.testTag("state_filter_$st")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                if (language == Language.TE) "ధృవీకరణ పత్రం లేదా ప్రభుత్వ సేవను నమోదు చేయండి..."
                                else "Enter a certificate or government service..."
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = CivicGreenPrimary)
                        },
                        trailingIcon = {
                            Button(
                                onClick = { onExecuteSearch(searchQuery) },
                                colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .height(36.dp)
                                    .testTag("execute_ai_search_button")
                            ) {
                                Text(if (language == Language.TE) "వెతకండి" else "Ask AI", fontSize = 12.sp)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("finder_search_input")
                    )
                }
            }
        }

        // AI Guidance Response Banner (when searched via AI)
        item {
            when (aiSearchState) {
                is AiSearchUiState.Loading -> {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = CivicGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (language == Language.TE) "అధికారిక శాఖల సమాచారాన్ని సరిచూస్తోంది..."
                                else "Analyzing official service database and requirements...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is AiSearchUiState.Success -> {
                    val resp = aiSearchState.response
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CivicGreenPrimaryContainer.copy(alpha = 0.4f)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicGreenPrimary.copy(alpha = 0.3f))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_response_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = CivicGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (resp.isFromGeminiAi) "Gemini Grounded Civic Guidance" else "Verified Knowledge Base Match",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CivicGreenPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CivicSurface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Official Truth Grounded",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CivicBlueSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = resp.explanationText,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Follow up question prompt if ambiguous
                            if (resp.isFollowUpNeeded && resp.followUpQuestion != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CivicAmberContainer),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CivicAmberTertiary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (language == Language.TE) "సందేహ నివృత్తి ప్రశ్న:" else "Clarification needed:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = CivicAmberTertiary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = resp.followUpQuestion,
                                            fontSize = 12.sp,
                                            color = CivicAmberOnContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is AiSearchUiState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusUrgentBg),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = aiSearchState.message,
                            color = StatusUrgentRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                AiSearchUiState.Idle -> {}
            }
        }

        // Search Results Header
        item {
            Text(
                text = if (language == Language.TE) "ధృవీకరించబడిన ఫలితాలు (${filteredServices.size})" else "Verified Results (${filteredServices.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (filteredServices.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.TE) "సరిపోలే సేవ కనుగొనబడలేదు" else "No matching service found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.TE) {
                                "తప్పుడు ప్రభుత్వ లింకులను అందించకుండా నిరోధించడానికి మా వ్యవస్థ అధికారిక డేటాబేస్ లోని వివరాలను మాత్రమే చూపుతుంది. జాతీయ పోర్టల్ చూడండి."
                            } else {
                                "To prevent hallucinated government links, we only display verified services from our controlled database. Please consult the National Services Portal."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://services.india.gov.in"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open National Portal (services.india.gov.in)", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredServices) { service ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_result_${service.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) service.nameTe else service.nameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${service.state} • ${service.districtRestriction}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            VerificationBadge(
                                isVerified = service.isVerified,
                                lastVerifiedDate = service.lastVerifiedDate
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Details grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "శాఖ:" else "Department:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == Language.TE) service.departmentTe else service.departmentEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "అధికారి:" else "Authority:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == Language.TE) service.issuingAuthorityTe else service.issuingAuthorityEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "రుసుము:" else "Fee:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(text = service.fee, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "కాలపరిమితి:" else "Processing Time:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(text = service.processingTime, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Documents Preview
                        Text(
                            text = if (language == Language.TE) "అవసరమైన పత్రాలు (${service.requiredDocuments.size}):" else "Required Documents (${service.requiredDocuments.size}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        service.requiredDocuments.take(3).forEach { doc ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.TE) doc.nameTe else doc.nameEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(service.officialPortalUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.TE) "పోర్టల్ తెరవండి" else "Open Portal", fontSize = 11.sp)
                            }

                            FilledTonalButton(
                                onClick = {
                                    onSelectService(service.id)
                                    onNavigate(Screen.DocumentChecklist)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.TE) "చెక్‌లిస్ట్" else "Checklist", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CivicWarningBanner(language = language)
                    }
                }
            }
        }
    }
}
