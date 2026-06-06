package com.healthapp.data.repository

import com.healthapp.data.local.dao.WaterRecordDao
import com.healthapp.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepository @Inject constructor(
    private val waterRecordDao: WaterRecordDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun today(): String = LocalDate.now().format(dateFormatter)

    fun getTodayRecords(): Flow<List<WaterRecord>> =
        waterRecordDao.getRecordsByDate(today())

    fun getTodayTotalAmount(): Flow<Int> =
        waterRecordDao.getTotalAmountByDate(today())

    fun getRecordsByDate(date: String): Flow<List<WaterRecord>> =
        waterRecordDao.getRecordsByDate(date)

    fun getAllDistinctDates(): Flow<List<String>> =
        waterRecordDao.getAllDistinctDates()

    suspend fun addWater(amount: Int, note: String? = null) {
        waterRecordDao.insertRecord(
            WaterRecord(date = today(), amount = amount, note = note)
        )
    }

    suspend fun deleteRecord(record: WaterRecord) = waterRecordDao.deleteRecord(record)

    /**
     * 获取最近7天每天的饮水量
     * @return List<Int> 按日期从旧到新排列，长度固定7
     */
    suspend fun getLast7DaysAmounts(): List<Int> {
        val today = LocalDate.now()
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong()).format(dateFormatter)
            waterRecordDao.getTotalAmountByDateOnce(date)
        }
    }
}
