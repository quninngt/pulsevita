package com.healthapp.util

object BmiUtils {
    fun calculateBmi(weightKg: Float, heightCm: Float): Float? {
        if (heightCm <= 0 || weightKg <= 0) return null
        val heightM = heightCm / 100
        return weightKg / (heightM * heightM)
    }

    fun getBmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "偏瘦"
            bmi < 24 -> "正常"
            bmi < 28 -> "偏胖"
            else -> "肥胖"
        }
    }

    fun getHealthyWeightRange(heightCm: Float): Pair<Float, Float>? {
        if (heightCm <= 0) return null
        val heightM = heightCm / 100
        val minWeight = 18.5f * heightM * heightM
        val maxWeight = 24f * heightM * heightM
        return Pair(minWeight, maxWeight)
    }
}
