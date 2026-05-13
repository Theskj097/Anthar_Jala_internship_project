package com.antharjala.watch.data.repository

import com.antharjala.watch.data.local.AlertDao
import com.antharjala.watch.data.local.BorewellDao
import com.antharjala.watch.data.local.SeedData
import com.antharjala.watch.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BorewellRepository(
    private val borewellDao: BorewellDao,
    private val alertDao: AlertDao
) {

    // ── Borewell Entries ─────────────────────────────────────────────────────

    val allEntries: Flow<List<BorewellEntry>> = borewellDao.getAllEntries()

    fun entriesForUser(userId: String): Flow<List<BorewellEntry>> =
        borewellDao.getEntriesForUser(userId)

    /**
     * Insert a new borewell reading.
     * Coordinates are snapped to a 500 m grid for privacy before storage.
     */
    suspend fun logBorewell(
        rawLat: Double,
        rawLng: Double,
        depthFt: Float,
        yearDrilled: Int,
        yieldInchesPerHour: Float,
        userId: String
    ) {
        val entry = BorewellEntry(
            lat = snapToGrid(rawLat),
            lng = snapToGrid(rawLng),
            depthFt = depthFt,
            yearDrilled = yearDrilled,
            yieldInchesPerHour = yieldInchesPerHour,
            userId = userId
        )
        borewellDao.insert(entry)
    }

    suspend fun countForUser(userId: String): Int =
        borewellDao.countForUser(userId)

    // ── Zone Aggregation ──────────────────────────────────────────────────────

    /**
     * Aggregate all borewell entries into per-zone [ZoneData] by grouping
     * entries that share the same grid-snapped (lat, lng) cell.
     */
    val zoneData: Flow<List<ZoneData>> = allEntries.map { entries ->
        entries
            .groupBy { Pair(it.lat, it.lng) }
            .map { (coords, group) ->
                val avgDepth = group.map { it.depthFt }.average().toFloat()
                val avgYield = group.map { it.yieldInchesPerHour }.average().toFloat()
                val stress = when {
                    avgYield >= 3f -> WaterStressLevel.SAFE
                    avgYield >= 1f -> WaterStressLevel.MODERATE
                    else           -> WaterStressLevel.CRITICAL
                }
                val weight = when (stress) {
                    WaterStressLevel.SAFE     -> 0.2
                    WaterStressLevel.MODERATE -> 0.6
                    WaterStressLevel.CRITICAL -> 1.0
                }
                ZoneData(
                    zoneLat = coords.first,
                    zoneLng = coords.second,
                    zoneName = "Zone (${String.format("%.4f", coords.first)}, ${String.format("%.4f", coords.second)})",
                    averageDepthFt = avgDepth,
                    averageYieldInchesPerHour = avgYield,
                    borewellCount = group.size,
                    stressLevel = stress,
                    heatmapWeight = weight
                )
            }
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    val allAlerts: Flow<List<AlertItem>> = alertDao.getAllAlerts()
    val unreadAlertCount: Flow<Int> = alertDao.unreadCount()

    suspend fun markAlertRead(id: Long) = alertDao.markRead(id)

    // ── Seed ──────────────────────────────────────────────────────────────────

    /**
     * Seeds demo data on first launch only.
     * Checks if DB already has records before inserting.
     */
    suspend fun seedIfEmpty() {
        if (borewellDao.countForUser("demo") == 0) {
            SeedData.borewellEntries.forEach { borewellDao.insert(it) }
        }
        if (alertDao.count() == 0) {
            alertDao.insertAll(SeedData.alerts)
        }
    }
}
