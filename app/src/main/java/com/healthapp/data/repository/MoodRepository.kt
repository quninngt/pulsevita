package com.healthapp.data.repository

import com.healthapp.data.local.dao.MoodRecordDao
import com.healthapp.data.local.entity.MoodRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val moodRecordDao: MoodRecordDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun today(): String = LocalDate.now().format(dateFormatter)

    fun getTodayRecords(): Flow<List<MoodRecord>> =
        moodRecordDao.getRecordsByDate(today())

    fun getRecentRecords(limit: Int = 7): Flow<List<MoodRecord>> =
        moodRecordDao.getRecentRecords(limit)

    fun getAllDistinctDates(): Flow<List<String>> =
        moodRecordDao.getAllDistinctDates()

    suspend fun addMood(moodLevel: Int, moodIcon: String, note: String? = null) {
        moodRecordDao.insertRecord(
            MoodRecord(date = today(), moodLevel = moodLevel, moodIcon = moodIcon, note = note)
        )
    }

    suspend fun deleteRecord(record: MoodRecord) = moodRecordDao.deleteRecord(record)
}
