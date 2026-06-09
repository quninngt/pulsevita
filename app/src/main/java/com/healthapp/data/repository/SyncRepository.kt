package com.healthapp.data.repository

import android.util.Log
import com.healthapp.data.local.dao.WaterRecordDao
import com.healthapp.data.local.dao.ExerciseRecordDao
import com.healthapp.data.local.dao.MoodRecordDao
import com.healthapp.data.local.dao.DietRecordDao
import com.healthapp.data.local.dao.UserDao
import com.healthapp.data.local.entity.UserEntity
import com.healthapp.data.remote.ServerApiService
import com.healthapp.data.remote.TokenManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据同步仓库
 * 负责本地数据与服务端的双向同步
 */
@Singleton
class SyncRepository @Inject constructor(
    private val serverApiService: ServerApiService,
    private val waterRecordDao: WaterRecordDao,
    private val exerciseRecordDao: ExerciseRecordDao,
    private val moodRecordDao: MoodRecordDao,
    private val dietRecordDao: DietRecordDao,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val tag = "SyncRepository"

    /**
     * 登录后自动同步本地数据到服务端
     * 静默执行，失败不影响用户体验
     */
    suspend fun syncOnLogin() {
        try {
            val today = LocalDate.now()
            val weekAgo = today.minusDays(7)
            val startDate = weekAgo.format(dateFormatter)
            val endDate = today.format(dateFormatter)
            
            val records = mutableListOf<Map<String, Any>>()
            
            // 收集饮水记录（最近 7 天）
            try {
                val allWater = waterRecordDao.getAllRecords()
                val recentWater = allWater.filter { it.date in startDate..endDate }
                recentWater.forEach { record ->
                    records.add(mapOf(
                        "type" to "water",
                        "date" to record.date,
                        "amount" to record.amount,
                        "timestamp" to record.timestamp
                    ))
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to collect water records: ${e.message}")
            }
            
            // 收集运动记录
            try {
                val allExercise = exerciseRecordDao.getAllRecords()
                val recentExercise = allExercise.filter { it.date in startDate..endDate }
                recentExercise.forEach { record ->
                    records.add(mapOf(
                        "type" to "exercise",
                        "date" to record.date,
                        "duration" to record.duration,
                        "exerciseType" to record.type,
                        "timestamp" to record.timestamp
                    ))
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to collect exercise records: ${e.message}")
            }
            
            // 收集心情记录
            try {
                val allMood = moodRecordDao.getAllRecords()
                val recentMood = allMood.filter { it.date in startDate..endDate }
                recentMood.forEach { record ->
                    records.add(mapOf(
                        "type" to "mood",
                        "date" to record.date,
                        "level" to record.moodLevel,
                        "note" to (record.note ?: ""),
                        "timestamp" to record.timestamp
                    ))
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to collect mood records: ${e.message}")
            }
            
            // 收集饮食记录
            try {
                val allDiet = dietRecordDao.getAllRecords()
                val recentDiet = allDiet.filter { it.date in startDate..endDate }
                recentDiet.forEach { record ->
                    records.add(mapOf(
                        "type" to "diet",
                        "date" to record.date,
                        "mealType" to record.mealType,
                        "description" to (record.description ?: ""),
                        "calories" to (record.calories ?: 0),
                        "timestamp" to record.timestamp
                    ))
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to collect diet records: ${e.message}")
            }
            
            // 如果有数据，同步到服务端
            if (records.isNotEmpty()) {
                try {
                    val response = serverApiService.syncRecords(records)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 200) {
                            Log.i(tag, "Sync successful: ${body.data?.syncedCount} synced, ${body.data?.skippedCount} skipped")
                        } else {
                            Log.w(tag, "Sync response error: ${body?.message}")
                        }
                    } else {
                        Log.w(tag, "Sync HTTP error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w(tag, "Sync network error: ${e.message}")
                }
            } else {
                Log.d(tag, "No records to sync")
            }

            // 从服务器拉取个人档案到本地
            fetchProfileFromServer()
        } catch (e: Exception) {
            Log.w(tag, "Sync on login failed: ${e.message}")
        }
    }

    /**
     * 从服务器拉取个人档案并保存到本地数据库
     * 用于卸载重装后恢复用户数据
     */
    suspend fun fetchProfileFromServer() {
        try {
            val response = serverApiService.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == 200 && body.data != null) {
                    val profile = body.data!!
                    val username = tokenManager.getUsername() ?: ""

                    // 检查本地是否已有用户数据
                    val existingUser = userDao.getCurrentUserOnce()

                    val userEntity = UserEntity(
                        id = existingUser?.id ?: java.util.UUID.randomUUID().toString(),
                        name = profile.nickname.ifEmpty { username },
                        gender = profile.gender ?: "",
                        height = profile.height ?: 0f,
                        weight = profile.weight ?: 0f,
                        occupation = profile.occupation ?: "",
                        createdAt = existingUser?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    if (existingUser != null) {
                        userDao.updateUser(userEntity)
                        Log.i(tag, "Profile updated from server")
                    } else {
                        userDao.insertUser(userEntity)
                        Log.i(tag, "Profile saved from server")
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to fetch profile: ${e.message}")
        }
    }

    /**
     * 将本地个人档案同步到服务器
     */
    suspend fun syncProfileToServer() {
        try {
            val user = userDao.getCurrentUserOnce() ?: return
            val profile = com.healthapp.data.remote.UserProfileData(
                userId = "",
                username = "",
                nickname = user.name,
                gender = user.gender,
                height = user.height,
                weight = user.weight,
                birthDate = null,
                occupation = user.occupation,
                avatarUrl = null
            )
            serverApiService.updateProfile(profile)
            Log.i(tag, "Profile synced to server")
        } catch (e: Exception) {
            Log.w(tag, "Failed to sync profile: ${e.message}")
        }
    }
}
