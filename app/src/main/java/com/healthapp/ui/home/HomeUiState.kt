package com.healthapp.ui.home

import com.healthapp.data.local.entity.HealthTip

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
    val extras: DailyExtrasState = DailyExtrasState()
)
