package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DemoAppointment
import com.example.data.model.Language
import com.example.data.model.ServiceRecord
import com.example.ui.components.CivicWarningBanner
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun ApplicationGuidanceScreen(
    language: Language,
    services: List<ServiceRecord>,
    selectedServiceId: String,
    onBookDemoAppointment: (String, String, String, String, String, String) -> DemoAppointment,
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

    var citizenName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var centerName by remember { mutableStateOf("Vijayawada Central MeeSeva Kendra #04") }
    var appointmentDate by remember { mutableStateOf("2026-10-18") }
    var slotTime by remember { mutableStateOf("11:00 AM - 11:30 AM") }
    var generatedToken by remember { mutableStateOf<DemoAppointment?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val centers = listOf(
        "Vijayawada Central MeeSeva Kendra #04",
        "Visakhapatnam East Grama Sachivalayam",
        "Guntur Urban Ward Secretariat #12",
        "Tirupati Rural Citizen Service Center",
        "Kurnool Collectorate MeeSeva Facilitation"
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
            Column {
                Text(
                    text = if (language == Language.TE) "దరఖాస్తు మార్గదర్శకత్వం & డెమో అపాయింట్‌మెంట్" else "Application Guidance & Demo Token",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CivicGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.TE) {
                        "అధికారిక పోర్టల్ సమర్పణ దశలు, అప్లికేషన్ స్టేటస్ ట్రాకింగ్ మరియు సిమ్యులేటెడ్ టోకెన్ డెమో."
                    } else {
                        "Step-by-step submission steps, application tracking guidelines, and simulated demo token."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Service Header Card
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
                                text = "${currentService.departmentEn} • ${currentService.portalName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        VerificationBadge(isVerified = currentService.isVerified, lastVerifiedDate = currentService.lastVerifiedDate)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentService.officialPortalUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == Language.TE) "అధికారిక పోర్టల్" else "Official Portal", fontSize = 11.sp)
                        }

                        if (currentService.statusUrl != null) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentService.statusUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.TrackChanges, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.TE) "స్టేటస్ చెక్ చేయండి" else "Status Portal", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Official Steps
        item {
            Text(
                text = if (language == Language.TE) "అధికారిక దరఖాస్తు దశలు" else "Official Application Next Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val steps = if (language == Language.TE) currentService.applicationStepsTe else currentService.applicationStepsEn

        itemsIndexed(steps) { idx, stepText ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CivicGreenPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CivicGreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stepText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Official Application Status Guidance
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CivicBlueSecondaryContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicBlueSecondary.copy(alpha = 0.3f)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = CivicBlueSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.TE) "దరఖాస్తు స్థితిని ఎలా తనిఖీ చేయాలి?" else "How to Track Your Application Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CivicBlueSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == Language.TE) {
                            "1. మీరు దరఖాస్తు సమర్పించిన తర్వాత లభించే 15-అంకెల రసీదు నంబర్ (Acknowledgement Number) ను భద్రపరచండి.\n2. AP MeeSeva పోర్టల్ లేదా సచివాలయంలో 'Track Application' క్లిక్ చేసి నంబర్ నమోదు చేయండి.\n3. తహసీల్దార్ ఆమోదించిన తర్వాత డిజిటల్ సర్టిఫికెట్ డౌన్‌లోడ్ చేసుకోండి."
                        } else {
                            "1. Keep your 15-digit Application Acknowledgement Number safe after online or Kendra submission.\n2. Open the official MeeSeva Portal and locate 'Track Application Status'.\n3. Enter your Application Number to view real-time stage approval by VRO, RI, and Tahsildar."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // SIMULATED APPOINTMENT / TOKEN WORKFLOW (SECTION 9)
        item {
            Column {
                Text(
                    text = if (language == Language.TE) "సిమ్యులేటెడ్ మీసేవ టోకెన్ డెమో" else "Simulated MeeSeva Token Demo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Mandatory Demo Notice Banner
                DemoNoticeBanner(language = language)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.TE) "డెమో టోకెన్ వివరాలు" else "Generate Simulated Kendra Token",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = citizenName,
                        onValueChange = { citizenName = it },
                        label = { Text(if (language == Language.TE) "పౌరుని పేరు" else "Citizen Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("demo_citizen_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (language == Language.TE) "మొబైల్ నంబర్" else "Mobile Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("demo_citizen_phone_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == Language.TE) "కేంద్రాన్ని ఎంచుకోండి:" else "Select Facilitation Center:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    centers.forEach { c ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (centerName == c) CivicGreenPrimaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            RadioButton(
                                selected = centerName == c,
                                onClick = { centerName = c }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = c, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cName = if (citizenName.isBlank()) "Citizen Applicant" else citizenName
                            val cPhone = if (phone.isBlank()) "9876543210" else phone
                            val token = onBookDemoAppointment(
                                cName,
                                cPhone,
                                currentService.nameEn,
                                centerName,
                                appointmentDate,
                                slotTime
                            )
                            generatedToken = token
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_demo_token_button")
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == Language.TE) "డెమో టోకెన్ రూపొందించండి" else "Generate Demo Token", fontSize = 13.sp)
                    }
                }
            }
        }

        // Active Generated Token Card
        if (generatedToken != null) {
            item {
                val token = generatedToken!!
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicGreenPrimaryContainer.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CivicGreenPrimary)),
                    modifier = Modifier.testTag("generated_token_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SIMULATED TOKEN SLIP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = CivicGreenPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StatusUrgentBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("DEMO ONLY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusUrgentRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = token.tokenNumber,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicGreenPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = "Applicant: ${token.citizenName} • ${token.phone}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Service: ${token.serviceName}", fontSize = 12.sp)
                        Text(text = "Center: ${token.centerName}", fontSize = 12.sp)
                        Text(text = "Date & Slot: ${token.appointmentDate} • ${token.slotTime}", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Please carry printed checklist documents to counter when visiting. This demo record has been saved to 'My Requests'.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            CivicWarningBanner(language = language)
        }
    }
}
