package com.healthapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Diet : Screen("diet")
    object Exercise : Screen("exercise")
    object Mental : Screen("mental")
    object Profile : Screen("profile")
    object WaterTracker : Screen("water_tracker")
    object ExerciseRecord : Screen("exercise_record")
    object MoodDiary : Screen("mood_diary")
    object DietRecord : Screen("diet_record")
    object BreathingExercise : Screen("breathing_exercise")
    object OfficeExercise : Screen("office_exercise")
}
