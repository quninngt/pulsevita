package com.healthapp.data.remote

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.*

// ===== Generic API response wrapper =====
data class ServerApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

// ===== Auth Data Models =====

data class LoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val nickname: String
)

data class UserProfileData(
    val userId: String,
    val username: String,
    val nickname: String,
    val gender: String?,
    val height: Float?,
    val weight: Float?,
    val birthDate: String?,
    val occupation: String?,
    val avatarUrl: String?
)

data class SyncData(
    val syncedCount: Int,
    val skippedCount: Int
)

data class ReportData(
    val id: String,
    val reportType: String,
    val period: String,
    val content: String,
    val summary: String,
    val createdAt: String
)

data class AchievementData(
    val code: String,
    val name: String,
    val description: String,
    val icon: String,
    val tier: String,
    val unlocked: Boolean,
    val unlockedAt: String?
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

// ===== Existing Data Models =====

/** 每日建议数据 */
data class DailySuggestionResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "date") val date: String,
    @Json(name = "suggestions") val suggestions: List<SuggestionData>,
    @Json(name = "userVotedSuggestionId") val userVotedSuggestionId: Long? = null
)

data class SuggestionData(
    @Json(name = "id") val id: Long,
    @Json(name = "icon") val icon: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String = "",
    @Json(name = "difficulty") val difficulty: String = ""
)

/** 投票请求 */
data class VoteRequest(
    @Json(name = "dailySuggestionId") val dailySuggestionId: Long,
    @Json(name = "suggestionId") val suggestionId: Long
)

/** 通用成功响应 */
data class SimpleApiResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null
)

/** 优化计划 */
data class PlanItem(
    @Json(name = "id") val id: Long,
    @Json(name = "suggestionId") val suggestionId: Long,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "progress") val progress: Int = 0,
    @Json(name = "completed") val completed: Boolean = false,
    @Json(name = "completedDate") val completedDate: String? = null,
    @Json(name = "createdAt") val createdAt: String = ""
)

/** 更新进度请求 */
data class UpdateProgressRequest(
    @Json(name = "progress") val progress: Int
)

/** 健康报告 */
data class HealthReport(
    @Json(name = "id") val id: Long,
    @Json(name = "type") val type: String,
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String,
    @Json(name = "summary") val summary: String = "",
    @Json(name = "startDate") val startDate: String = "",
    @Json(name = "endDate") val endDate: String = "",
    @Json(name = "createdAt") val createdAt: String = ""
)

/** 报告列表响应 */
data class ReportListResponse(
    @Json(name = "reports") val reports: List<HealthReport>,
    @Json(name = "page") val page: Int,
    @Json(name = "totalPages") val totalPages: Int = 1
)

/** 成就 */
data class AchievementItem(
    @Json(name = "id") val id: Long,
    @Json(name = "icon") val icon: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "unlocked") val unlocked: Boolean,
    @Json(name = "progress") val progress: Float = 0f
)

/** 添加计划请求 */
data class AddPlanRequest(
    @Json(name = "suggestionId") val suggestionId: Long
)

// ===== API Service Interface =====

/**
 * 服务端 API 接口
 * 提供建议、计划、报告、成就等服务
 */
interface ServerApiService {

    // ---- Auth ----

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ServerApiResponse<LoginData>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ServerApiResponse<LoginData>>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: Map<String, String>): Response<ServerApiResponse<LoginData>>

    // ---- User Profile ----

    @GET("api/user/profile")
    suspend fun getProfile(): Response<ServerApiResponse<UserProfileData>>

    @PUT("api/user/profile")
    suspend fun updateProfile(@Body profile: UserProfileData): Response<ServerApiResponse<UserProfileData>>

    // ---- Suggestions ----

    /** 获取每日建议 */
    @GET("api/suggestions/daily")
    suspend fun getDailySuggestions(
        @Query("date") date: String
    ): DailySuggestionResponse

    /** 投票 */
    @POST("api/suggestions/vote")
    suspend fun vote(
        @Body request: VoteRequest
    ): SimpleApiResponse

    // ---- Plans ----

    /** 获取活跃计划列表 */
    @GET("api/plan/active")
    suspend fun getActivePlans(): List<PlanItem>

    /** 获取所有计划列表 */
    @GET("api/plan/all")
    suspend fun getAllPlans(): List<PlanItem>

    /** 添加计划 */
    @POST("api/plan/add")
    suspend fun addPlan(
        @Body request: AddPlanRequest
    ): PlanItem

    /** 更新计划进度 */
    @PUT("api/plan/{planId}/progress")
    suspend fun updateProgress(
        @Path("planId") planId: Long,
        @Body request: UpdateProgressRequest
    ): PlanItem

    /** 完成计划 */
    @POST("api/plan/{planId}/complete")
    suspend fun completePlan(
        @Path("planId") planId: Long
    ): PlanItem

    // ---- Reports ----

    /** 获取最新报告 */
    @GET("api/reports/latest")
    suspend fun getLatestReport(
        @Query("type") type: String
    ): HealthReport?

    /** 获取报告列表 */
    @GET("api/reports/list")
    suspend fun getReportList(
        @Query("type") type: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ReportListResponse

    // ---- Achievements ----

    /** 获取所有成就 */
    @GET("api/achievements/all")
    suspend fun getAllAchievements(): List<AchievementItem>

    // ---- Records ----

    @POST("api/records/sync")
    suspend fun syncRecords(@Body records: Any): Response<ServerApiResponse<SyncData>>

    @GET("api/records/pull")
    suspend fun pullRecords(@Query("since") since: String): Response<ServerApiResponse<Any>>

    @GET("api/records/stats/weekly")
    suspend fun getWeeklyStats(): Response<ServerApiResponse<Any>>

    // ---- Content ----

    @GET("api/content/tips/daily")
    suspend fun getDailyTip(): Response<ServerApiResponse<Any>>

    @GET("api/content/challenges/daily")
    suspend fun getDailyChallenge(@Query("date") date: String): Response<ServerApiResponse<Any>>
}
