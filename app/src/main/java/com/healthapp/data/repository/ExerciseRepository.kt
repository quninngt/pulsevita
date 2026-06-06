package com.healthapp.data.repository

import com.healthapp.data.local.dao.ExerciseRecordDao
import com.healthapp.data.local.entity.ExerciseRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseRecordDao: ExerciseRecordDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun today(): String = LocalDate.now().format(dateFormatter)

    fun getTodayRecords(): Flow<List<ExerciseRecord>> =
        exerciseRecordDao.getRecordsByDate(today())

    fun getTodayTotalDuration(): Flow<Int> =
        exerciseRecordDao.getTotalDurationByDate(today())

    fun getTodayTotalSteps(): Flow<Int> =
        exerciseRecordDao.getTotalStepsByDate(today())

    fun getRecordsByDate(date: String): Flow<List<ExerciseRecord>> =
        exerciseRecordDao.getRecordsByDate(date)

    fun getAllDistinctDates(): Flow<List<String>> =
        exerciseRecordDao.getAllDistinctDates()

    suspend fun addExercise(type: String, duration: Int, steps: Int? = null, note: String? = null) {
        exerciseRecordDao.insertRecord(
            ExerciseRecord(date = today(), type = type, duration = duration, steps = steps, note = note)
        )
    }

    suspend fun deleteRecord(record: ExerciseRecord) = exerciseRecordDao.deleteRecord(record)
}
