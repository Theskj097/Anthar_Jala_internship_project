package com.antharjala.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AlertSeverity { INFO, WARNING, CRITICAL }

/**
 * An area-level water alert pushed or seeded by the system.
 * Alerts are community-scoped — they never reveal individual borewell data.
 */
@Entity(tableName = "alerts")
data class AlertItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val zoneName: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
