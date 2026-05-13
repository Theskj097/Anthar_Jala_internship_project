package com.antharjala.watch.data.local

import androidx.room.*
import com.antharjala.watch.data.model.BorewellEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BorewellDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BorewellEntry): Long

    @Query("SELECT * FROM borewell_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BorewellEntry>>

    @Query("SELECT * FROM borewell_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getEntriesForUser(userId: String): Flow<List<BorewellEntry>>

    @Query("SELECT COUNT(*) FROM borewell_entries WHERE userId = :userId")
    suspend fun countForUser(userId: String): Int

    @Query("DELETE FROM borewell_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
