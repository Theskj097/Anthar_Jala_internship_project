package com.antharjala.watch.ui.alerts

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antharjala.watch.data.model.AlertItem
import com.antharjala.watch.data.model.AlertSeverity
import com.antharjala.watch.ui.theme.*
import com.antharjala.watch.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertsScreen(viewModel: MainViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    val unreadCount by viewModel.unreadAlertCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepOcean)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(StressCritical.copy(alpha = 0.3f), DeepOcean)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null,
                        tint = StressCritical, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Water Alerts",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold
                    )
                    if (unreadCount > 0) {
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(StressCritical),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$unreadCount", color = Color.White,
                                fontWeight = FontWeight.Bold, fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Area-level groundwater notifications",
                    style = MaterialTheme.typography.bodyMedium, color = TextSecondary
                )
            }
        }

        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null,
                        tint = StressSafe, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No active alerts for your area.", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alerts, key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        onMarkRead = { viewModel.markAlertRead(alert.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: AlertItem, onMarkRead: () -> Unit) {
    val (bgColor, borderColor, icon) = when (alert.severity) {
        AlertSeverity.CRITICAL -> Triple(
            StressCritical.copy(alpha = 0.1f), StressCritical, Icons.Default.Warning
        )
        AlertSeverity.WARNING  -> Triple(
            StressModerate.copy(alpha = 0.1f), StressModerate, Icons.Default.Info
        )
        AlertSeverity.INFO     -> Triple(
            CyanAccent.copy(alpha = 0.08f), CyanAccent, Icons.Default.Notifications
        )
    }
    val accentColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> StressCritical
        AlertSeverity.WARNING  -> StressModerate
        AlertSeverity.INFO     -> CyanAccent
    }
    val severityLabel = when (alert.severity) {
        AlertSeverity.CRITICAL -> "CRITICAL"
        AlertSeverity.WARNING  -> "WARNING"
        AlertSeverity.INFO     -> "INFO"
    }
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(
            width = if (!alert.isRead) 1.dp else 0.5.dp,
            color = if (!alert.isRead) borderColor else DividerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                severityLabel,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "📍 ${alert.zoneName}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        df.format(Date(alert.timestamp)),
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }

                if (!alert.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                alert.message,
                color = if (alert.isRead) TextSecondary else TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            if (!alert.isRead) {
                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = onMarkRead,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.DoneAll, null,
                        tint = accentColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Mark as Read", color = accentColor, fontSize = 12.sp)
                }
            }
        }
    }
}
