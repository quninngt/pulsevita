package com.healthapp.data.local.dao

import androidx.room.*
import com.healthapp.data.local.entity.ExerciseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseRecordDao {
    @Query("SELECT * FROM exercise_records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<ExerciseRecord>>

    @Query("SELECT COALESCE(SUM(duration), 0) FROM exercise_records WHERE date = :date")
    fun getTotalDurationByDate(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(steps), 0) FROM exercise_records WHERE date = :date")
    fun getTotalStepsByDate(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ExerciseRecord)

    @Delete
    suspend fun deleteRecord(record: ExerciseRecord)

    @Query("SELECT DISTINCT date FROM exercise_records ORDER BY date DESC")
    fun getAllDistinctDates(): Flow<List<String>>
}
