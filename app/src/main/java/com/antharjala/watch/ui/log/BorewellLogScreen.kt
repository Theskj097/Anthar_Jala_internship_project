package com.antharjala.watch.ui.log

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antharjala.watch.data.model.BorewellEntry
import com.antharjala.watch.data.model.WaterStressLevel
import com.antharjala.watch.data.model.stressLevel
import com.antharjala.watch.ui.theme.*
import com.antharjala.watch.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorewellLogScreen(viewModel: MainViewModel) {
    val logSuccess by viewModel.logSuccess.collectAsState()
    val userEntries by viewModel.userEntries.collectAsState()

    var depthText by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("") }
    var yieldText by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    // Realistic GPS for Kolar district (hardcoded for demo — real app uses FusedLocation)
    val demoLat = 13.1440
    val demoLng = 78.1305

    LaunchedEffect(logSuccess) {
        if (logSuccess) {
            depthText = ""
            yearText = ""
            yieldText = ""
            showErrors = false
            viewModel.resetLogSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepOcean)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(WaterBlue.copy(alpha = 0.6f), DeepOcean)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null,
                        tint = CyanAccent, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Log Your Borewell",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Your data helps the community track water health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Vertical Depth Scale + Form ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Animated depth scale visual
            val depth = depthText.toFloatOrNull() ?: 0f
            VerticalDepthScale(
                currentDepth = depth,
                maxDepth = 400f,
                modifier = Modifier
                    .width(56.dp)
                    .height(280.dp)
            )

            Spacer(Modifier.width(16.dp))

            // Form fields
            Column(modifier = Modifier.weight(1f)) {
                AntharTextField(
                    value = depthText,
                    onValueChange = { depthText = it },
                    label = "Current Depth (ft)",
                    placeholder = "e.g. 200",
                    icon = Icons.Default.ArrowDownward,
                    keyboardType = KeyboardType.Number,
                    isError = showErrors && depthText.toFloatOrNull() == null
                )
                Spacer(Modifier.height(12.dp))
                AntharTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = "Year Drilled",
                    placeholder = "e.g. 2018",
                    icon = Icons.Default.CalendarMonth,
                    keyboardType = KeyboardType.Number,
                    isError = showErrors && (yearText.toIntOrNull()
                        ?.let { it < 1950 || it > Calendar.getInstance().get(Calendar.YEAR) } != false)
                )
                Spacer(Modifier.height(12.dp))
                AntharTextField(
                    value = yieldText,
                    onValueChange = { yieldText = it },
                    label = "Current Yield (in/hr)",
                    placeholder = "e.g. 2.5",
                    icon = Icons.Default.WaterDrop,
                    keyboardType = KeyboardType.Decimal,
                    isError = showErrors && yieldText.toFloatOrNull() == null
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Yield indicator ───────────────────────────────────────────────────
        val yieldVal = yieldText.toFloatOrNull()
        if (yieldVal != null) {
            val stressColor = when {
                yieldVal >= 3f -> StressSafe
                yieldVal >= 1f -> StressModerate
                else           -> StressCritical
            }
            val stressLabel = when {
                yieldVal >= 3f -> "✅ Yield is Safe"
                yieldVal >= 1f -> "⚠️ Yield is Moderate — Monitor closely"
                else           -> "🔴 Yield is Critical — Consider recharge"
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = stressColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(stressColor)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stressLabel, color = stressColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Privacy note ──────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp),
            color = SurfaceCard
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Shield, contentDescription = null,
                    tint = CyanAccent, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Privacy: Your exact location is hidden. GPS is snapped to a 500 m grid before saving.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Submit Button ─────────────────────────────────────────────────────
        Button(
            onClick = {
                showErrors = true
                val depth = depthText.toFloatOrNull()
                val year = yearText.toIntOrNull()
                val yield = yieldText.toFloatOrNull()
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                if (depth != null && year != null && year in 1950..currentYear && yield != null) {
                    viewModel.logBorewell(
                        lat = demoLat, lng = demoLng,
                        depthFt = depth, yearDrilled = year, yieldInchesPerHour = yield
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepOcean)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Submit Reading", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(24.dp))

        // ── History toggle ────────────────────────────────────────────────────
        if (userEntries.isNotEmpty()) {
            TextButton(
                onClick = { showHistory = !showHistory },
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Icon(
                    if (showHistory) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "My Borewell History (${userEntries.size})",
                    color = CyanAccent, fontWeight = FontWeight.SemiBold
                )
            }

            if (showHistory) {
                userEntries.forEach { entry ->
                    BorewellHistoryCard(entry = entry)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun VerticalDepthScale(
    currentDepth: Float,
    maxDepth: Float,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = (currentDepth / maxDepth).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic),
        label = "depth_fill"
    )

    val stressColor = when {
        currentDepth == 0f    -> CyanAccent
        currentDepth < 150f   -> StressSafe
        currentDepth < 250f   -> StressModerate
        else                  -> StressCritical
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "DEPTH",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 8.sp
        )
        Spacer(Modifier.height(4.dp))

        // Scale bar
        Box(
            modifier = Modifier
                .weight(1f)
                .width(24.dp)
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, DividerColor, RoundedCornerShape(12.dp))
            )

            // Water fill (from bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedFraction)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(stressColor.copy(alpha = 0.3f), stressColor)
                        )
                    )
                    .align(Alignment.BottomCenter)
            )

            // Tick marks
            Canvas(modifier = Modifier.fillMaxSize()) {
                val tickCount = 4
                for (i in 1 until tickCount) {
                    val y = size.height * i / tickCount
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            if (currentDepth > 0f) "${currentDepth.toInt()}ft" else "0",
            style = MaterialTheme.typography.labelSmall,
            color = stressColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AntharTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = DividerColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = CyanAccent,
            focusedLabelColor = CyanAccent,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            errorBorderColor = StressCritical,
            errorLabelColor = StressCritical
        ),
        singleLine = true
    )
}

@Composable
private fun BorewellHistoryCard(entry: BorewellEntry) {
    val stressColor = when (entry.stressLevel()) {
        WaterStressLevel.SAFE     -> StressSafe
        WaterStressLevel.MODERATE -> StressModerate
        WaterStressLevel.CRITICAL -> StressCritical
    }
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(stressColor)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Depth: ${entry.depthFt.toInt()} ft · Drilled: ${entry.yearDrilled}",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    "Yield: ${entry.yieldInchesPerHour} in/hr · ${df.format(Date(entry.timestamp))}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = stressColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
