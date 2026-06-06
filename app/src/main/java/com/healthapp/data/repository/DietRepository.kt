package com.healthapp.data.repository

import com.healthapp.data.local.dao.DietRecordDao
import com.healthapp.data.local.entity.DietRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DietRepository @Inject constructor(
    private val dietRecordDao: DietRecordDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun today(): String = LocalDate.now().format(dateFormatter)

    fun getTodayRecords(): Flow<List<DietRecord>> =
        dietRecordDao.getRecordsByDate(today())

    fun getRecordsByDateAndMeal(date: String, mealType: String): Flow<List<DietRecord>> =
        dietRecordDao.getRecordsByDateAndMeal(date, mealType)

    suspend fun addDiet(mealType: String, description: String, calories: Int? = null) {
        dietRecordDao.insertRecord(
            DietRecord(date = today(), mealType = mealType, description = description, calories = calories)
        )
    }

    suspend fun deleteRecord(record: DietRecord) = dietRecordDao.deleteRecord(record)
}
