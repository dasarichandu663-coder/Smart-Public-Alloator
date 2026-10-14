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
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceRecord
import com.example.ui.components.CivicWarningBanner
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    language: Language,
    services: List<ServiceRecord>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onSelectService: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val incomeCert = services.find { it.id == "ap_income_cert" }

    val quickQueries = if (language == Language.TE) {
        listOf(
            "ఆదాయ ధృవీకరణ పత్రం (Income Certificate)",
            "కుల ధృవీకరణ పత్రం (Caste Certificate)",
            "జనన ధృవీకరణ పత్రం (Birth Certificate)",
            "బియ్యం కార్డు (Rice Card)",
            "డ్రైవింగ్ లైసెన్స్ (LLR)"
        )
    } else {
        listOf(
            "Which department issues an Income Certificate?",
            "Where can I apply for a caste certificate?",
            "How do I get a birth certificate?",
            "What documents do I need for a ration card?",
            "How can I check my application status?"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Civic Title & Subtitle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CivicGreenPrimaryContainer.copy(alpha = 0.5f))
                    .border(1.dp, CivicGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (language == Language.TE) {
                        "సరైన ప్రభుత్వ సేవను కనుగొనండి. మీ తదుపరి అడుగును తెలుసుకోండి."
                    } else {
                        "Find the Right Government Service. Know Your Next Step."
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CivicGreenPrimary,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == Language.TE) {
                        "సేవలను గుర్తించడానికి, పత్రాలను సిద్ధం చేసుకోవడానికి మరియు అధికారిక ప్రభుత్వ పోర్టల్‌లను చేరుకోవడానికి సరళమైన మార్గదర్శకత్వం."
                    } else {
                        "Simple guidance to help you identify services, prepare documents, and reach official government portals."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Prominent Search Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchSubmit(searchQuery) }) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = CivicGreenPrimary)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == Language.TE) "సాధారణ ప్రశ్నలు:" else "Common questions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(quickQueries) { chip ->
                            SuggestionChip(
                                onClick = {
                                    onSearchQueryChange(chip)
                                    onSearchSubmit(chip)
                                    onNavigate(Screen.ServiceFinder)
                                },
                                label = { Text(chip, fontSize = 11.sp, maxLines = 1) },
                                modifier = Modifier.testTag("chip_${chip.take(10)}")
                            )
                        }
                    }
                }
            }
        }

        // SECTION 5: ANDHRA PRADESH INCOME CERTIFICATE DEMO CARD
        item {
            if (incomeCert != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicGreenPrimary.copy(alpha = 0.4f))),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.testTag("ap_income_cert_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == Language.TE) incomeCert.nameTe else incomeCert.nameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "State: ${incomeCert.state} • ${incomeCert.districtRestriction}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            VerificationBadge(
                                isVerified = incomeCert.isVerified,
                                lastVerifiedDate = incomeCert.lastVerifiedDate
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Key metadata items
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "బాధ్యతాయుత శాఖ:" else "Responsible Department:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == Language.TE) incomeCert.departmentTe else incomeCert.departmentEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "ఎక్కడ దరఖాస్తు చేయాలి:" else "Where to Apply:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = incomeCert.portalName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = CivicBlueSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "చట్టబద్ధమైన రుసుము:" else "Statutory Fee:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(text = incomeCert.fee, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.TE) "కాలపరిమితి:" else "Processing Time:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(text = incomeCert.processingTime, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Open Official Portal & Checklist
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(incomeCert.officialPortalUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_official_portal_button")
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == Language.TE) "అధికారిక పోర్టల్ తెరవండి" else "Open Official Portal",
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    onSelectService(incomeCert.id)
                                    onNavigate(Screen.DocumentChecklist)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_checklist_button")
                            ) {
                                Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == Language.TE) "పత్రాల చెక్‌లిస్ట్" else "Document Checklist",
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mandatory warning
                        CivicWarningBanner(language = language)
                    }
                }
            }
        }

        // Service Categories Overview
        item {
            Text(
                text = if (language == Language.TE) "ప్రభుత్వ శాఖల వారీగా సేవలు" else "Browse Services by Department",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(ServiceCategory.values().toList()) { category ->
            val matchingCount = services.count { it.category == category }
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSearchQueryChange(category.displayNameEn)
                        onSearchSubmit(category.displayNameEn)
                        onNavigate(Screen.ServiceFinder)
                    }
                    .testTag("category_card_${category.name}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CivicBlueSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (category) {
                                    ServiceCategory.REVENUE -> Icons.Default.AccountBalance
                                    ServiceCategory.CIVIL_SUPPLIES -> Icons.Default.ShoppingCart
                                    ServiceCategory.MUNICIPAL -> Icons.Default.LocationCity
                                    ServiceCategory.TRANSPORT -> Icons.Default.DirectionsCar
                                    ServiceCategory.SOCIAL_WELFARE -> Icons.Default.VolunteerActivism
                                    ServiceCategory.GENERAL -> Icons.Default.Public
                                },
                                contentDescription = null,
                                tint = CivicBlueSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == Language.TE) category.displayNameTe else category.displayNameEn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$matchingCount verified service records",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // SEVA Challenge Information Card
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CivicSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SEVA First Innovation Challenge Project",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CivicGreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Built by a 5-member undergraduate engineering team. Features verified Andhra Pradesh civic services, bilingual Telugu/English guidance, Gemini AI service query resolution, and classical deterministic staff workload scheduling.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
