package com.healthapp.ui.home

import com.healthapp.data.local.entity.HealthTip
import com.healthapp.ui.components.Achievement
import java.time.LocalDate

/** Weather + location info */
data class WeatherState(
    val temperature: String = "",
    val weatherEmoji: String = "",
    val city: String = "",
    val solarTerm: String = "",
    val solarTermTip: String = ""
)

/** Water intake tracking */
data class WaterState(
    val amount: Int = 0,
    val goal: Int = 2000
)

/** Exercise tracking */
data class ExerciseState(
    val duration: Int = 0,
    val goal: Int = 30,
    val steps: Int = 0,
    val stepsGoal: Int = 8000
)

/** Mood tracking */
data class MoodState(
    val level: Int? = null,
    val icon: String = ""
)

/** Daily extras: challenge, food, quote */
data class DailyExtrasState(
    val challengeTitle: String = "",
    val challengeDesc: String = "",
    val challengeIcon: String = "",
    val foodName: String = "",
    val foodProperty: String = "",
    val foodPropertyDesc: String = "",
    val foodBenefits: String = "",
    val foodHowToEat: String = "",
    val quote: String = "",
    val quoteFrom: String = ""
)

/** Composite state for the Home screen */
data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val greeting: String = "",
    val userName: String = "",
    val streakDays: Int = 0,
    val healthTip: HealthTip? = null,
    val weather: WeatherState = WeatherState(),
    val water: WaterState = WaterState(),
    val exercise: ExerciseState = ExerciseState(),
    val mood: MoodState = MoodState(),
    val extras: DailyExtrasState = DailyExtrasState(),
    // === 新增：打卡日历数据 ===
    val streakDates: Set<LocalDate> = emptySet(),
    // === 新增：7天趋势数据 ===
    val weeklyWaterAmounts: List<Int> = emptyList(),
    val weeklyExerciseMinutes: List<Int> = emptyList(),
    val weeklyMoodLevels: List<Int?> = emptyList(),
    // === 新增：成就系统 ===
    val achievements: List<Achievement> = emptyList(),
    // === 新增：周概览数据 ===
    val weeklyWaterDays: Int = 0,
    val weeklyExerciseDays: Int = 0,
    val weeklyMoodDays: Int = 0,
    val weeklyDietDays: Int = 0,
    val weeklyWaterTrend: String = "→",
    val weeklyExerciseTrend: String = "→",
    val weeklyMoodTrend: String = "→",
    val weeklyDietTrend: String = "→"
)
