package com.antharjala.watch.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antharjala.watch.data.model.WaterStressLevel
import com.antharjala.watch.data.model.ZoneData
import com.antharjala.watch.ui.theme.*
import com.antharjala.watch.viewmodel.MainViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng

@Composable
fun WaterMapScreen(viewModel: MainViewModel) {
    val zoneData by viewModel.zoneData.collectAsState()
    var selectedZone by remember { mutableStateOf<ZoneData?>(null) }

    // Village center: Kolar district, Karnataka
    val villageCenter = LatLng(13.1390, 78.1305)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(villageCenter, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Google Map ────────────────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Heatmap overlay via MapEffect (native GoogleMap API)
            MapEffect(key1 = zoneData) { map ->
                if (zoneData.isNotEmpty()) {
                    val weightedPoints = zoneData.map { zone ->
                        WeightedLatLng(
                            LatLng(zone.zoneLat, zone.zoneLng),
                            zone.heatmapWeight
                        )
                    }
                    val provider = HeatmapTileProvider.Builder()
                        .weightedData(weightedPoints)
                        .radius(50)
                        .opacity(0.75)
                        .build()
                    map.addTileOverlay(
                        TileOverlayOptions().tileProvider(provider)
                    )
                }
            }

            // Marker for each zone (tappable)
            zoneData.forEach { zone ->
                val markerColor = when (zone.stressLevel) {
                    WaterStressLevel.SAFE     -> StressSafe
                    WaterStressLevel.MODERATE -> StressModerate
                    WaterStressLevel.CRITICAL -> StressCritical
                }
                Marker(
                    state = MarkerState(position = LatLng(zone.zoneLat, zone.zoneLng)),
                    title = zone.zoneName,
                    onClick = {
                        selectedZone = zone
                        true
                    }
                )
            }
        }

        // ── Top header overlay ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepOcean.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Community Water Map",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "${zoneData.size} zones tracked · Tap a marker for details",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Legend
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepOcean.copy(alpha = 0.82f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(StressSafe, "Safe")
                    LegendDot(StressModerate, "Moderate")
                    LegendDot(StressCritical, "Critical")
                }
            }
        }

        // ── Zone detail bottom sheet ──────────────────────────────────────────
        AnimatedVisibility(
            visible = selectedZone != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedZone?.let { zone ->
                ZoneDetailCard(zone = zone, onDismiss = { selectedZone = null })
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun ZoneDetailCard(zone: ZoneData, onDismiss: () -> Unit) {
    val stressColor = when (zone.stressLevel) {
        WaterStressLevel.SAFE     -> StressSafe
        WaterStressLevel.MODERATE -> StressModerate
        WaterStressLevel.CRITICAL -> StressCritical
    }
    val stressLabel = when (zone.stressLevel) {
        WaterStressLevel.SAFE     -> "✅ SAFE"
        WaterStressLevel.MODERATE -> "⚠️ MODERATE"
        WaterStressLevel.CRITICAL -> "🔴 CRITICAL"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceCard,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Zone Water Health",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${zone.borewellCount} borewell${if (zone.borewellCount > 1) "s" else ""} in cluster",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stress badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = stressColor.copy(alpha = 0.15f)
            ) {
                Text(
                    stressLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = stressColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ZoneStatChip(
                    label = "Avg Depth",
                    value = "${zone.averageDepthFt.toInt()} ft",
                    color = CyanAccent
                )
                ZoneStatChip(
                    label = "Avg Yield",
                    value = "${String.format("%.1f", zone.averageYieldInchesPerHour)} in/hr",
                    color = stressColor
                )
                ZoneStatChip(
                    label = "Borewells",
                    value = "${zone.borewellCount}",
                    color = TealAccent
                )
            }

            Spacer(Modifier.height(8.dp))

            // Depth bar
            DepthBar(
                currentDepth = zone.averageDepthFt,
                maxDepth = 400f,
                color = stressColor
            )
        }
    }
}

@Composable
private fun ZoneStatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun DepthBar(currentDepth: Float, maxDepth: Float, color: Color) {
    val fraction = (currentDepth / maxDepth).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0 ft (Surface)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text("${maxDepth.toInt()} ft (Deep)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DividerColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Current avg depth: ${currentDepth.toInt()} ft",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

