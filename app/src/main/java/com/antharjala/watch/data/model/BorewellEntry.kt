package com.antharjala.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single borewell reading submitted by a user.
 *
 * Privacy: [lat] and [lng] are snapped to the nearest 500m grid cell
 * BEFORE this entity is created, so exact locations are never persisted.
 */
@Entity(tableName = "borewell_entries")
data class BorewellEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Grid-snapped latitude (500m resolution) */
    val lat: Double,

    /** Grid-snapped longitude (500m resolution) */
    val lng: Double,

    /** Borewell depth in feet at time of drilling */
    val depthFt: Float,

    /** Year the borewell was drilled */
    val yearDrilled: Int,

    /** Current water yield in inches per hour */
    val yieldInchesPerHour: Float,

    /** Timestamp of this reading (epoch ms) */
    val timestamp: Long = System.currentTimeMillis(),

    /** Anonymous local user identifier (UUID) */
    val userId: String = ""
)

/**
 * Normalized stress level computed from yield.
 */
enum class WaterStressLevel {
    SAFE,       // yield > 3 in/hr  — Green
    MODERATE,   // yield 1–3 in/hr  — Yellow
    CRITICAL    // yield < 1 in/hr  — Red
}

fun BorewellEntry.stressLevel(): WaterStressLevel = when {
    yieldInchesPerHour >= 3f -> WaterStressLevel.SAFE
    yieldInchesPerHour >= 1f -> WaterStressLevel.MODERATE
    else                     -> WaterStressLevel.CRITICAL
}

/** Heatmap weight: 0.0 (safe) → 1.0 (critical) */
fun BorewellEntry.heatmapWeight(): Double = when (stressLevel()) {
    WaterStressLevel.SAFE     -> 0.2
    WaterStressLevel.MODERATE -> 0.6
    WaterStressLevel.CRITICAL -> 1.0
}
