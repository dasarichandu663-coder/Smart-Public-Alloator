package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.TaskPriority
import com.example.ui.theme.*

@Composable
fun VerificationBadge(
    isVerified: Boolean,
    lastVerifiedDate: String,
    modifier: Modifier = Modifier
) {
    if (isVerified) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(StatusVerifiedBg)
                .border(1.dp, StatusVerifiedGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Official",
                tint = StatusVerifiedGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Verified Official • $lastVerifiedDate",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StatusVerifiedGreen
            )
        }
    } else {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(StatusDemoBg)
                .border(1.dp, StatusDemoAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Demo Information",
                tint = StatusDemoAmber,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Demo — verification required",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StatusDemoAmber
            )
        }
    }
}

@Composable
fun CivicWarningBanner(
    language: Language,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CivicAmberContainer.copy(alpha = 0.6f))
            .border(1.dp, CivicAmberTertiary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Notice",
            tint = CivicAmberTertiary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (language == Language.TE) {
                "నిబంధనలు మరియు పత్రాల వివరాలు మారవచ్చు. తాజా సమాచారం కోసం అధికారిక మీసేవ పోర్టల్ లేదా సమీప సచివాలయాన్ని సంప్రదించండి."
            } else {
                "Requirements may change. Confirm the latest details on the official portal or with an authorized MeeSeva centre."
            },
            fontSize = 12.sp,
            color = CivicAmberOnContainer,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun DemoNoticeBanner(
    language: Language,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StatusUrgentBg)
            .border(1.dp, StatusUrgentRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Demo Notice",
            tint = StatusUrgentRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (language == Language.TE) {
                "డెమో మాత్రమే — ఇది నిజమైన ప్రభుత్వ అపాయింట్‌మెంట్ లేదా ధృవీకరణ పత్రం కాదు."
            } else {
                "DEMO ONLY — This is not a real government appointment or official certificate."
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = StatusUrgentRed
        )
    }
}

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (priority) {
        TaskPriority.URGENT -> StatusUrgentBg to StatusUrgentRed
        TaskPriority.HIGH -> CivicAmberContainer to CivicAmberTertiary
        TaskPriority.MEDIUM -> CivicBlueSecondaryContainer to CivicBlueSecondary
        TaskPriority.LOW -> CivicSurfaceVariant to CivicOnSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = priority.displayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
