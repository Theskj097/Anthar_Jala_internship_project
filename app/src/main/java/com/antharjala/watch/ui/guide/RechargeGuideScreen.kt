package com.antharjala.watch.ui.guide

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.antharjala.watch.ui.theme.*

data class GuideMethod(
    val id: Int,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val steps: List<String>,
    val diagramType: DiagramType
)

enum class DiagramType { PERCOLATION_PIT, BOREWELL_RECHARGE, FARM_POND }

val guideMethods = listOf(
    GuideMethod(
        id = 1,
        title = "Percolation Pit",
        subtitle = "Simple trench to collect rainwater",
        icon = Icons.Default.Layers,
        color = TealAccent,
        steps = listOf(
            "Choose a low-lying area 3–5 meters from any structure.",
            "Dig a pit of 1m × 1m × 1.5m (L×W×D) or larger.",
            "Fill the bottom 30 cm with coarse gravel (5–10mm stones).",
            "Add a layer of sand (20 cm) on top of the gravel.",
            "Cover with pebbles or perforated concrete slab.",
            "Direct rainwater runoff from roof/field into the pit.",
            "Clean and de-silt annually before each monsoon."
        ),
        diagramType = DiagramType.PERCOLATION_PIT
    ),
    GuideMethod(
        id = 2,
        title = "Borewell Recharge",
        subtitle = "Feed treated water into an existing borewell",
        icon = Icons.Default.WaterDrop,
        color = CyanAccent,
        steps = listOf(
            "Install a 'T' junction at the borewell casing top.",
            "Connect a 4-inch PVC pipe from rooftop gutters to the T-junction.",
            "Add a 3-stage filter: mesh screen → gravel → sand before the inlet.",
            "Install a first-flush diverter to discard the first 20 litres of rain.",
            "Ensure the filter chamber is cleaned every 2 weeks during rains.",
            "Do NOT recharge with soapy or chemically contaminated water.",
            "Seal the junction when not using to prevent mosquito breeding."
        ),
        diagramType = DiagramType.BOREWELL_RECHARGE
    ),
    GuideMethod(
        id = 3,
        title = "Farm Pond",
        subtitle = "Large-scale water storage for agricultural areas",
        icon = Icons.Default.Agriculture,
        color = StressSafe,
        steps = listOf(
            "Select a site at the lowest point of the farm catchment area.",
            "Dig a pond of approx 20m × 15m × 3m (depends on land size).",
            "Line the sides with compacted clay or low-cost HDPE lining.",
            "Create a gently sloped inlet channel from the field boundary.",
            "Plant Vetiver grass along the pond banks to prevent erosion.",
            "Install an overflow pipe to redirect excess water to the field.",
            "Stock with fish to monitor water quality and utilize the pond."
        ),
        diagramType = DiagramType.FARM_POND
    )
)

@Composable
fun RechargeGuideScreen() {
    var expandedId by remember { mutableStateOf<Int?>(1) }

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
                        colors = listOf(TealAccent.copy(alpha = 0.5f), DeepOcean)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, null, tint = TealAccent, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Recharge Guide",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "3 DIY methods to replenish groundwater in your village",
                    style = MaterialTheme.typography.bodyMedium, color = TextSecondary
                )
            }
        }

        // ── Impact Stats Bar ──────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImpactStat("1 Pit", "saves 50,000L\nper monsoon", TealAccent)
                ImpactStat("Recharge", "raises table by\n5–15 ft/year", CyanAccent)
                ImpactStat("1 Pond", "serves 10+\nborewells nearby", StressSafe)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Guide Cards ───────────────────────────────────────────────────────
        guideMethods.forEach { method ->
            val isExpanded = expandedId == method.id
            GuideMethodCard(
                method = method,
                isExpanded = isExpanded,
                onToggle = { expandedId = if (isExpanded) null else method.id }
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ImpactStat(title: String, subtitle: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(subtitle, color = TextSecondary, fontSize = 11.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun GuideMethodCard(
    method: GuideMethod,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = if (isExpanded) BorderStroke(1.dp, method.color.copy(alpha = 0.5f)) else null
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(method.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(method.icon, null, tint = method.color, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(method.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(method.subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null, tint = method.color, modifier = Modifier.size(24.dp)
                )
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                    HorizontalDivider(color = DividerColor, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    // Diagram
                    Text("Diagram", color = method.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OceanBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        when (method.diagramType) {
                            DiagramType.PERCOLATION_PIT    -> PercolationPitDiagram(method.color)
                            DiagramType.BOREWELL_RECHARGE  -> BorewellRechargeDiagram(method.color)
                            DiagramType.FARM_POND          -> FarmPondDiagram(method.color)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Steps
                    Text("Steps", color = method.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    method.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(method.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    color = method.color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(step, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp,
                                modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ── Canvas Diagrams ────────────────────────────────────────────────────────────

@Composable
private fun PercolationPitDiagram(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        // Ground surface
        drawLine(Color.White.copy(alpha = 0.3f), Offset(0f, h * 0.35f), Offset(w, h * 0.35f), 2f)

        // Pit outline
        val pitLeft = w * 0.3f; val pitRight = w * 0.7f
        val pitTop = h * 0.35f; val pitBottom = h * 0.9f
        drawRect(
            color = color.copy(alpha = 0.2f),
            topLeft = Offset(pitLeft, pitTop),
            size = Size(pitRight - pitLeft, pitBottom - pitTop)
        )
        drawRect(
            color = color, topLeft = Offset(pitLeft, pitTop),
            size = Size(pitRight - pitLeft, pitBottom - pitTop),
            style = Stroke(2f)
        )

        // Gravel layer
        drawRect(
            color = Color(0xFFD4A017).copy(alpha = 0.5f),
            topLeft = Offset(pitLeft + 2f, pitBottom - (pitBottom - pitTop) * 0.35f),
            size = Size(pitRight - pitLeft - 4f, (pitBottom - pitTop) * 0.35f)
        )

        // Sand layer
        drawRect(
            color = Color(0xFFE8C97A).copy(alpha = 0.4f),
            topLeft = Offset(pitLeft + 2f, pitBottom - (pitBottom - pitTop) * 0.6f),
            size = Size(pitRight - pitLeft - 4f, (pitBottom - pitTop) * 0.25f)
        )

        // Water arrows (rain falling in)
        for (i in 0..2) {
            val x = pitLeft + (pitRight - pitLeft) * (i + 1) / 4f
            drawLine(color.copy(alpha = 0.8f), Offset(x, h * 0.05f), Offset(x, h * 0.3f), 2f)
            // Arrow head
            drawLine(color.copy(alpha = 0.8f), Offset(x, h * 0.3f), Offset(x - 5f, h * 0.22f), 2f)
            drawLine(color.copy(alpha = 0.8f), Offset(x, h * 0.3f), Offset(x + 5f, h * 0.22f), 2f)
        }

        // Labels
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 20f
                setColor(android.graphics.Color.argb(180, 255, 255, 255))
            }
            drawText("Sand", w * 0.5f, pitBottom - (pitBottom - pitTop) * 0.47f, paint)
            paint.color = android.graphics.Color.argb(180, 212, 160, 23)
            drawText("Gravel", w * 0.5f, pitBottom - (pitBottom - pitTop) * 0.15f, paint)
            paint.color = android.graphics.Color.argb(150, 180, 220, 255)
            drawText("Rain ↓", w * 0.5f, h * 0.15f, paint)
        }
    }
}

@Composable
private fun BorewellRechargeDiagram(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width; val h = size.height

        // Borewell casing (vertical pipe)
        val bwX = w * 0.55f
        drawRect(
            color = Color.Gray.copy(alpha = 0.5f),
            topLeft = Offset(bwX - 10f, h * 0.3f),
            size = Size(20f, h * 0.65f),
            style = Stroke(2f)
        )

        // Filter box
        val fLeft = w * 0.15f; val fRight = w * 0.45f
        val fTop = h * 0.25f; val fBottom = h * 0.65f
        drawRect(color = color.copy(alpha = 0.15f), topLeft = Offset(fLeft, fTop),
            size = Size(fRight - fLeft, fBottom - fTop))
        drawRect(color = color, topLeft = Offset(fLeft, fTop),
            size = Size(fRight - fLeft, fBottom - fTop), style = Stroke(2f))

        // Filter layers
        val layerH = (fBottom - fTop) / 3f
        for (i in 1..2) {
            drawLine(color.copy(alpha = 0.3f),
                Offset(fLeft, fTop + layerH * i), Offset(fRight, fTop + layerH * i), 1.5f)
        }

        // Connection pipe (filter → borewell)
        drawLine(color.copy(alpha = 0.8f), Offset(fRight, fTop + (fBottom - fTop) / 2f),
            Offset(bwX - 10f, fTop + (fBottom - fTop) / 2f), 4f)

        // Water drops from top into filter
        for (i in 0..1) {
            val x = fLeft + (fRight - fLeft) * (i + 1) / 3f
            drawCircle(color.copy(0.7f), 6f, Offset(x, fTop - 20f))
        }

        // Ground line
        drawLine(Color.White.copy(0.3f), Offset(0f, h * 0.3f), Offset(w, h * 0.3f), 1.5f)

        drawContext.canvas.nativeCanvas.apply {
            val p = android.graphics.Paint().apply {
                textSize = 18f; textAlign = android.graphics.Paint.Align.CENTER
                setColor(android.graphics.Color.argb(180, 255, 255, 255))
            }
            drawText("Filter", (fLeft + fRight) / 2f, (fTop + fBottom) / 2f, p)
            drawText("Borewell", bwX, h * 0.95f, p)
            p.color = android.graphics.Color.argb(160, 180, 220, 255)
            drawText("Rain", (fLeft + fRight) / 2f, fTop - 28f, p)
        }
    }
}

@Composable
private fun FarmPondDiagram(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width; val h = size.height

        // Ground
        drawLine(Color.White.copy(0.3f), Offset(0f, h * 0.4f), Offset(w, h * 0.4f), 1.5f)

        // Pond shape (trapezoid cross-section)
        val path = Path().apply {
            moveTo(w * 0.05f, h * 0.4f)
            lineTo(w * 0.2f, h * 0.85f)
            lineTo(w * 0.8f, h * 0.85f)
            lineTo(w * 0.95f, h * 0.4f)
            close()
        }
        drawPath(path, color.copy(alpha = 0.15f))
        drawPath(path, color, style = Stroke(2f))

        // Water inside
        val waterPath = Path().apply {
            moveTo(w * 0.25f, h * 0.68f)
            lineTo(w * 0.3f, h * 0.82f)
            lineTo(w * 0.7f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.68f)
            close()
        }
        drawPath(waterPath, Color(0xFF1565C0).copy(alpha = 0.5f))

        // Inlet channel (from left)
        drawLine(color.copy(0.6f), Offset(0f, h * 0.35f), Offset(w * 0.15f, h * 0.4f), 3f)

        // Overflow pipe (right)
        drawRect(Color.Gray.copy(0.5f),
            topLeft = Offset(w * 0.86f, h * 0.36f), size = Size(16f, 8f))

        // Grass on banks
        for (i in 0..5) {
            val x = w * 0.04f + i * (w * 0.16f)
            drawLine(color.copy(0.5f), Offset(x, h * 0.4f), Offset(x + 3f, h * 0.3f), 2f)
            drawLine(color.copy(0.5f), Offset(x + 4f, h * 0.4f), Offset(x + 8f, h * 0.28f), 2f)
        }

        drawContext.canvas.nativeCanvas.apply {
            val p = android.graphics.Paint().apply {
                textSize = 19f; textAlign = android.graphics.Paint.Align.CENTER
                setColor(android.graphics.Color.argb(180, 255, 255, 255))
            }
            drawText("Farm Pond", w * 0.5f, h * 0.6f, p)
            p.textSize = 15f
            p.color = android.graphics.Color.argb(150, 180, 220, 255)
            drawText("Water", w * 0.5f, h * 0.77f, p)
            drawText("Inlet ↗", w * 0.1f, h * 0.28f, p)
            drawText("Overflow →", w * 0.87f, h * 0.3f, p)
        }
    }
}
