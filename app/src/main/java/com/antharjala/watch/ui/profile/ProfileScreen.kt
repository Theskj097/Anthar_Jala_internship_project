package com.antharjala.watch.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.antharjala.watch.ui.theme.*
import com.antharjala.watch.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val userId by viewModel.userId.collectAsState()
    val nickname by viewModel.nickname.collectAsState()
    val contributionCount by viewModel.contributionCount.collectAsState()
    val userEntries by viewModel.userEntries.collectAsState()

    var editingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }

    // Compute badge from contribution count
    val (badgeLabel, badgeColor, badgeIcon) = when {
        contributionCount >= 20 -> Triple("🏆 Guardian", StressSafe, Icons.Default.EmojiEvents)
        contributionCount >= 10 -> Triple("⭐ Contributor", CyanAccent, Icons.Default.Star)
        contributionCount >= 5  -> Triple("🌱 Watcher", StressModerate, Icons.Default.WaterDrop)
        else                    -> Triple("🌀 Newcomer", TextSecondary, Icons.Default.Person)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepOcean)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero header ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(WaterBlue.copy(alpha = 0.7f), DeepOcean)
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(CyanAccent.copy(0.3f), WaterBlue.copy(0.5f)))
                        )
                        .border(2.dp, CyanAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = CyanAccent, modifier = Modifier.size(44.dp))
                }

                Spacer(Modifier.height(12.dp))

                if (editingName) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Enter nickname") },
                            singleLine = true,
                            modifier = Modifier.width(180.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = DividerColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = CyanAccent,
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (nameInput.isNotBlank()) {
                                viewModel.updateNickname(nameInput.trim())
                            }
                            editingName = false
                        }) {
                            Icon(Icons.Default.Check, null, tint = StressSafe)
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            nickname,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { nameInput = nickname; editingName = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Anonymous ID display (truncated)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceCard
                ) {
                    Text(
                        "ID: ${userId.take(8)}•••",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Badge Card ────────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(badgeIcon, null, tint = badgeColor, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        badgeLabel,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "Contribution Badge",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Stats Row ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Readings Logged",
                value = "$contributionCount",
                color = CyanAccent,
                icon = Icons.Default.WaterDrop
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "This Week",
                value = "${userEntries.count { System.currentTimeMillis() - it.timestamp < 7 * 24 * 60 * 60 * 1000L }}",
                color = TealAccent,
                icon = Icons.Default.DateRange
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Next badge progress ───────────────────────────────────────────────
        val (nextTarget, nextLabel) = when {
            contributionCount < 5  -> Pair(5, "Watcher")
            contributionCount < 10 -> Pair(10, "Contributor")
            contributionCount < 20 -> Pair(20, "Guardian")
            else                   -> Pair(20, "Max Level")
        }
        val progress = (contributionCount.toFloat() / nextTarget).coerceIn(0f, 1f)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progress to $nextLabel", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        "$contributionCount / $nextTarget",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyanAccent,
                    trackColor = DividerColor
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Privacy Notice ────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = WaterBlue.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, WaterBlue.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, null,
                        tint = CyanAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Privacy Commitment",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(10.dp))
                PrivacyPoint("Your exact GPS location is never stored")
                PrivacyPoint("All data is snapped to a 500 m grid")
                PrivacyPoint("No real name, address, or phone collected")
                PrivacyPoint("Your ID is a random anonymous identifier")
                PrivacyPoint("Data is used only for community water health")
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(label, color = TextSecondary, fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun PrivacyPoint(text: String) {
    Row(
        modifier = Modifier.padding(bottom = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.CheckCircle, null,
            tint = StressSafe, modifier = Modifier.size(14.dp).padding(top = 2.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
    }
}
