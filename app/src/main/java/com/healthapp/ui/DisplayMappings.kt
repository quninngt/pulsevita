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
        else -> type
    }

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
