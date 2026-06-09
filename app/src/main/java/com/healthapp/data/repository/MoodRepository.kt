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

    /**
     * 获取最近N天每天的心情等级
     * @return List<Int?> 按日期从旧到新排列，长度固定days，无记录为null
     */
    suspend fun getLastNDaysMoodLevels(days: Int = 7): List<Int?> {
        val today = LocalDate.now()
        return ((days - 1) downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong()).format(dateFormatter)
            moodRecordDao.getLatestMoodLevelByDate(date)
        }
    }

    /**
     * 获取最近7天每天的心情等级（兼容旧调用）
     */
    suspend fun getLast7DaysMoodLevels(): List<Int?> = getLastNDaysMoodLevels(7)

    /**
     * 获取最近30天每天的心情等级
     */
    suspend fun getLast30DaysMoodLevels(): List<Int?> = getLastNDaysMoodLevels(30)
}
