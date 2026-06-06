package com.healthapp.data.repository

import com.healthapp.data.local.dao.HealthTipDao
import com.healthapp.data.local.entity.HealthTip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthTipRepository @Inject constructor(
    private val healthTipDao: HealthTipDao
) {
    fun getRandomTip(): Flow<HealthTip?> = healthTipDao.getRandomTip()

    fun getRandomTipByCategory(category: String): Flow<HealthTip?> {
        return healthTipDao.getRandomTipByCategory(category)
    }

    fun getRandomTipBySeason(season: String): Flow<HealthTip?> {
        return healthTipDao.getRandomTipBySeason(season)
    }
}
