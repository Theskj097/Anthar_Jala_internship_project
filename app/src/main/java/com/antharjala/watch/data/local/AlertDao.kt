package com.antharjala.watch.data.local

import androidx.room.*
import com.antharjala.watch.data.model.AlertItem
import com.antharjala.watch.data.model.AlertSeverity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(alerts: List<AlertItem>)

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertItem>>

    @Query("SELECT COUNT(*) FROM alerts")
    suspend fun count(): Int

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("SELECT COUNT(*) FROM alerts WHERE isRead = 0")
    fun unreadCount(): Flow<Int>
}
