package com.healthapp.util

object Constants {
    // Water tracking defaults
    const val DEFAULT_WATER_GOAL = 2000 // ml
    const val WATER_SMALL_AMOUNT = 100
    const val WATER_MEDIUM_AMOUNT = 250
    const val WATER_LARGE_AMOUNT = 500

    // Exercise defaults
    const val DEFAULT_EXERCISE_DURATION_GOAL = 30 // minutes
    const val DEFAULT_STEPS_GOAL = 8000

    // Mood levels
    const val MOOD_VERY_BAD = 1
    const val MOOD_BAD = 2
    const val MOOD_NEUTRAL = 3
    const val MOOD_GOOD = 4
    const val MOOD_VERY_GOOD = 5

    // Exercise types
    const val EXERCISE_WALKING = "walking"
    const val EXERCISE_OFFICE = "office_exercise"
    const val EXERCISE_YOGA = "yoga"

    // Meal types
    const val MEAL_BREAKFAST = "breakfast"
    const val MEAL_LUNCH = "lunch"
    const val MEAL_DINNER = "dinner"
    const val MEAL_SNACK = "snack"

    // Health tip categories
    const val TIP_DIET = "diet"
    const val TIP_EXERCISE = "exercise"
    const val TIP_MENTAL = "mental"
    const val TIP_TCM = "tcm"

    // Seasons
    const val SEASON_SPRING = "spring"
    const val SEASON_SUMMER = "summer"
    const val SEASON_AUTUMN = "autumn"
    const val SEASON_WINTER = "winter"

    // Breathing exercise
    const val BREATHING_INHALE_SECONDS = 4
    const val BREATHING_HOLD_SECONDS = 7
    const val BREATHING_EXHALE_SECONDS = 8
}
