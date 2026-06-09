package com.healthapp.ui

object DisplayMappings {

    fun moodLevelName(level: Int): String = when (level) {
        1 -> "很差"
        2 -> "较差"
        3 -> "一般"
        4 -> "较好"
        5 -> "很好"
        else -> ""
    }

    data class MoodOption(val level: Int, val icon: String)
    val moodOptions = listOf(
        MoodOption(1, "😞"),
        MoodOption(2, "😔"),
        MoodOption(3, "😐"),
        MoodOption(4, "🙂"),
        MoodOption(5, "😊")
    )

    fun exerciseTypeName(type: String): String = when (type) {
        "walking" -> "步行"
        "office_exercise" -> "办公室运动"
        "yoga" -> "瑜伽"
        "running" -> "跑步"
        "cycling" -> "骑行"
        "swimming" -> "游泳"
        "stretching" -> "拉伸"
        "strength" -> "力量训练"
        else -> type
    }

    data class ExerciseType(val key: String, val label: String, val icon: String)
    val exerciseTypes = listOf(
        ExerciseType("walking", "步行", "🚶"),
        ExerciseType("running", "跑步", "🏃"),
        ExerciseType("cycling", "骑行", "🚴"),
        ExerciseType("yoga", "瑜伽", "🧘"),
        ExerciseType("stretching", "拉伸", "🤸"),
        ExerciseType("strength", "力量训练", "💪"),
        ExerciseType("swimming", "游泳", "🏊"),
        ExerciseType("office_exercise", "办公室运动", "🪑")
    )

    data class MealType(val key: String, val label: String)
    val mealTypes = listOf(
        MealType("breakfast", "早餐"),
        MealType("lunch", "午餐"),
        MealType("dinner", "晚餐"),
        MealType("snack", "加餐")
    )

    fun mealTypeName(key: String): String =
        mealTypes.firstOrNull { it.key == key }?.label ?: key
}
