package com.healthapp.data.local.dao

import androidx.room.*
import com.healthapp.data.local.entity.HealthTip
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthTipDao {
    @Query("SELECT * FROM health_tips WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    fun getRandomTipByCategory(category: String): Flow<HealthTip?>

    @Query("SELECT * FROM health_tips ORDER BY RANDOM() LIMIT 1")
    fun getRandomTip(): Flow<HealthTip?>

    @Query("SELECT * FROM health_tips WHERE season = :season ORDER BY RANDOM() LIMIT 1")
    fun getRandomTipBySeason(season: String): Flow<HealthTip?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: HealthTip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<HealthTip>)
}
