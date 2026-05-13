package com.antharjala.watch.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core Palette ──────────────────────────────────────────────────────────────
val DeepOcean      = Color(0xFF0A1628)
val OceanBlue      = Color(0xFF0D2137)
val WaterBlue      = Color(0xFF1565C0)
val CyanAccent     = Color(0xFF00BCD4)
val TealAccent     = Color(0xFF00897B)

// ── Stress Status Colors ──────────────────────────────────────────────────────
val StressSafe     = Color(0xFF4CAF50)   // green  — yield > 3 in/hr
val StressModerate = Color(0xFFFFC107)   // amber  — yield 1-3 in/hr
val StressCritical = Color(0xFFF44336)   // red    — yield < 1 in/hr

// ── Text & Surface ────────────────────────────────────────────────────────────
val TextPrimary    = Color(0xFFFFFFFF)
val TextSecondary  = Color(0xFFB0C4DE)
val SurfaceCard    = Color(0xFF132337)
val SurfaceElevated= Color(0xFF1A2E45)
val DividerColor   = Color(0xFF1E3A5F)

// ── Material3 token mappings ──────────────────────────────────────────────────
val Primary        = CyanAccent
val OnPrimary      = DeepOcean
val PrimaryContainer = Color(0xFF003D4D)
val OnPrimaryContainer = Color(0xFF9EF6FF)

val Secondary      = TealAccent
val OnSecondary    = DeepOcean
val SecondaryContainer = Color(0xFF003730)
val OnSecondaryContainer = Color(0xFF9EF2E4)

val Background     = DeepOcean
val OnBackground   = TextPrimary
val Surface        = OceanBlue
val OnSurface      = TextPrimary
val SurfaceVariant = SurfaceCard
val OnSurfaceVariant = TextSecondary

val Error          = StressCritical
val OnError        = Color.White
val ErrorContainer = Color(0xFF4B0000)
val OnErrorContainer= Color(0xFFFFB4AB)

val Outline        = DividerColor
val OutlineVariant = Color(0xFF1A3A5C)
