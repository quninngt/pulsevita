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

    /**
     * 获取最近7天每天的运动时长
     * @return List<Int> 按日期从旧到新排列，长度固定7
     */
    suspend fun getLast7DaysMinutes(): List<Int> {
        val today = LocalDate.now()
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong()).format(dateFormatter)
            exerciseRecordDao.getTotalDurationByDateOnce(date)
        }
    }
}
