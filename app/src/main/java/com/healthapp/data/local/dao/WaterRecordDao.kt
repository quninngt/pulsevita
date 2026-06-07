package com.healthapp.data.local.dao

import androidx.room.*
import com.healthapp.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterRecordDao {
    @Query("SELECT * FROM water_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<WaterRecord>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM water_records WHERE date = :date")
    fun getTotalAmountByDate(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WaterRecord)

    @Delete
    suspend fun deleteRecord(record: WaterRecord)

    @Query("DELETE FROM water_records WHERE date = :date")
    suspend fun deleteAllByDate(date: String)

    @Query("SELECT DISTINCT date FROM water_records ORDER BY date DESC")
    fun getAllDistinctDates(): Flow<List<String>>

    @Query("SELECT * FROM water_records ORDER BY date DESC, timestamp DESC")
    suspend fun getAllRecords(): List<WaterRecord>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM water_records WHERE date = :date")
    suspend fun getTotalAmountByDateOnce(date: String): Int
}
