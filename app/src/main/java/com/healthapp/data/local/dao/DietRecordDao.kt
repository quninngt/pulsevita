package com.healthapp.data.local.dao

import androidx.room.*
import com.healthapp.data.local.entity.DietRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DietRecordDao {
    @Query("SELECT * FROM diet_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<DietRecord>>

    @Query("SELECT * FROM diet_records WHERE date = :date AND mealType = :mealType")
    fun getRecordsByDateAndMeal(date: String, mealType: String): Flow<List<DietRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DietRecord)

    @Delete
    suspend fun deleteRecord(record: DietRecord)

    @Query("SELECT * FROM diet_records ORDER BY date DESC, timestamp DESC")
    suspend fun getAllRecords(): List<DietRecord>

    @Query("SELECT * FROM diet_records WHERE date >= :fromDate ORDER BY date DESC, timestamp DESC")
    suspend fun getRecordsAfterDate(fromDate: String): List<DietRecord>
}
