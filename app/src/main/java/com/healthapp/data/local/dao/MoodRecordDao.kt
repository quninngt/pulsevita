package com.healthapp.data.local.dao

import androidx.room.*
import com.healthapp.data.local.entity.MoodRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodRecordDao {
    @Query("SELECT * FROM mood_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<MoodRecord>>

    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 7): Flow<List<MoodRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MoodRecord)

    @Delete
    suspend fun deleteRecord(record: MoodRecord)

    @Query("SELECT DISTINCT date FROM mood_records ORDER BY date DESC")
    fun getAllDistinctDates(): Flow<List<String>>

    @Query("SELECT * FROM mood_records ORDER BY date DESC, timestamp DESC")
    suspend fun getAllRecords(): List<MoodRecord>

    @Query("SELECT moodLevel FROM mood_records WHERE date = :date ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMoodLevelByDate(date: String): Int?
}
