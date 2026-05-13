package com.antharjala.watch.data.model

/**
 * Aggregated water health data for a GPS cluster / zone.
 * Computed in-memory from [BorewellEntry] records — not persisted.
 */
data class ZoneData(
    val zoneLat: Double,
    val zoneLng: Double,
    val zoneName: String,
    val averageDepthFt: Float,
    val averageYieldInchesPerHour: Float,
    val borewellCount: Int,
    val stressLevel: WaterStressLevel,

    /** 0.0 – 1.0 weight used for heatmap intensity */
    val heatmapWeight: Double
)

/** Snap a coordinate to the nearest 500 m grid cell (≈ 0.0045°) */
fun snapToGrid(coord: Double, gridSizeDegrees: Double = 0.0045): Double {
    return Math.round(coord / gridSizeDegrees) * gridSizeDegrees
}
