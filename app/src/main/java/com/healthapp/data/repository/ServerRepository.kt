package com.healthapp.data.repository

import com.healthapp.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepository @Inject constructor(
    private val serverApiService: ServerApiService,
    private val tokenManager: TokenManager
) {
    /** 获取每日建议 */
    suspend fun getDailySuggestions(date: String): NetworkResult<DailySuggestionResponse> {
        return safeApiCall {
            val response = serverApiService.getDailySuggestions(date)
            if (response.code == 200 && response.data != null) response.data
            else throw Exception(response.message)
        }
    }

    /** 投票 */
    suspend fun vote(dailySuggestionId: Long, suggestionId: Long): NetworkResult<Boolean> {
        return safeApiCall {
            val response = serverApiService.vote(VoteRequest(dailySuggestionId, suggestionId))
            if (response.code == 200) true
            else throw Exception(response.message)
        }
    }

    /** 获取活跃计划 */
    suspend fun getActivePlans(): NetworkResult<List<PlanItem>> {
        return safeApiCall {
            val response = serverApiService.getActivePlans()
            if (response.code == 200) response.data ?: emptyList()
            else throw Exception(response.message)
        }
    }

    /** 获取所有计划 */
    suspend fun getAllPlans(): NetworkResult<List<PlanItem>> {
        return safeApiCall {
            val response = serverApiService.getAllPlans()
            if (response.code == 200) response.data ?: emptyList()
            else throw Exception(response.message)
        }
    }

    /** 添加计划 */
    suspend fun addPlan(suggestionId: Long): NetworkResult<PlanItem> {
        return safeApiCall {
            val response = serverApiService.addPlan(AddPlanRequest(suggestionId))
            if (response.code == 200 && response.data != null) response.data
            else throw Exception(response.message)
        }
    }

    /** 更新计划进度 */
    suspend fun updateProgress(planId: Long, progress: Int): NetworkResult<PlanItem> {
        return safeApiCall {
            val response = serverApiService.updateProgress(planId, UpdateProgressRequest(progress))
            if (response.code == 200 && response.data != null) response.data
            else throw Exception(response.message)
        }
    }

    /** 完成计划 */
    suspend fun completePlan(planId: Long): NetworkResult<PlanItem> {
        return safeApiCall {
            val response = serverApiService.completePlan(planId)
            if (response.code == 200 && response.data != null) response.data
            else throw Exception(response.message)
        }
    }

    /** 获取最新报告 */
    suspend fun getLatestReport(type: String): NetworkResult<HealthReport?> {
        return safeApiCall {
            val response = serverApiService.getLatestReport(type)
            if (response.code == 200) response.data
            else throw Exception(response.message)
        }
    }

    /** 获取报告列表 */
    suspend fun getReportList(type: String, page: Int = 1): NetworkResult<List<HealthReport>> {
        return safeApiCall {
            val response = serverApiService.getReportList(type, page)
            if (response.code == 200) response.data ?: emptyList()
            else throw Exception(response.message)
        }
    }

    /** 获取所有成就 */
    suspend fun getAllAchievements(): NetworkResult<List<AchievementItem>> {
        return safeApiCall {
            val response = serverApiService.getAllAchievements()
            if (response.code == 200) response.data ?: emptyList()
            else throw Exception(response.message)
        }
    }

    /** 获取每日健康贴士 */
    suspend fun getDailyTip(): NetworkResult<DailyTipData?> {
        return safeApiCall {
            val response = serverApiService.getDailyTip()
            if (response.isSuccessful) response.body()?.data else null
        }
    }

    /** 获取每日挑战 */
    suspend fun getDailyChallenge(date: String): NetworkResult<DailyChallengeData?> {
        return safeApiCall {
            val response = serverApiService.getDailyChallenge(date)
            if (response.isSuccessful) response.body()?.data else null
        }
    }
}
