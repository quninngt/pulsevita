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
        return safeApiCall { serverApiService.getDailySuggestions(date) }
    }

    /** 投票 */
    suspend fun vote(dailySuggestionId: Long, suggestionId: Long): NetworkResult<SimpleApiResponse> {
        return safeApiCall { serverApiService.vote(VoteRequest(dailySuggestionId, suggestionId)) }
    }

    /** 获取活跃计划 */
    suspend fun getActivePlans(): NetworkResult<List<PlanItem>> {
        return safeApiCall { serverApiService.getActivePlans() }
    }

    /** 获取所有计划 */
    suspend fun getAllPlans(): NetworkResult<List<PlanItem>> {
        return safeApiCall { serverApiService.getAllPlans() }
    }

    /** 添加计划 */
    suspend fun addPlan(suggestionId: Long): NetworkResult<PlanItem> {
        return safeApiCall { serverApiService.addPlan(AddPlanRequest(suggestionId)) }
    }

    /** 更新计划进度 */
    suspend fun updateProgress(planId: Long, progress: Int): NetworkResult<PlanItem> {
        return safeApiCall { serverApiService.updateProgress(planId, UpdateProgressRequest(progress)) }
    }

    /** 完成计划 */
    suspend fun completePlan(planId: Long): NetworkResult<PlanItem> {
        return safeApiCall { serverApiService.completePlan(planId) }
    }

    /** 获取最新报告 */
    suspend fun getLatestReport(type: String): NetworkResult<HealthReport?> {
        return safeApiCall { serverApiService.getLatestReport(type) }
    }

    /** 获取报告列表 */
    suspend fun getReportList(type: String, page: Int = 1): NetworkResult<ReportListResponse> {
        return safeApiCall { serverApiService.getReportList(type, page) }
    }

    /** 获取所有成就 */
    suspend fun getAllAchievements(): NetworkResult<List<AchievementItem>> {
        return safeApiCall { serverApiService.getAllAchievements() }
    }
}
