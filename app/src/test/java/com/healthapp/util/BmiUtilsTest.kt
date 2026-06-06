package com.healthapp.util

import org.junit.Test
import org.junit.Assert.*

class BmiUtilsTest {

    @Test
    fun `calculateBMI with valid values returns correct BMI`() {
        // Given
        val weight = 70f // kg
        val height = 175f // cm

        // When
        val bmi = BmiUtils.calculateBmi(weight, height)

        // Then
        assertNotNull(bmi)
        assertEquals(22.86f, bmi!!, 0.01f)
    }

    @Test
    fun `calculateBMI with zero height returns null`() {
        // Given
        val weight = 70f
        val height = 0f

        // When
        val bmi = BmiUtils.calculateBmi(weight, height)

        // Then
        assertNull(bmi)
    }

    @Test
    fun `calculateBMI with zero weight returns null`() {
        // Given
        val weight = 0f
        val height = 175f

        // When
        val bmi = BmiUtils.calculateBmi(weight, height)

        // Then
        assertNull(bmi)
    }

    @Test
    fun `calculateBMI with negative values returns null`() {
        // Given
        val weight = -70f
        val height = 175f

        // When
        val bmi = BmiUtils.calculateBmi(weight, height)

        // Then
        assertNull(bmi)
    }

    @Test
    fun `getBmiCategory returns underweight for BMI less than 18`() {
        // Given
        val bmi = 17.5f

        // When
        val category = BmiUtils.getBmiCategory(bmi)

        // Then
        assertEquals("偏瘦", category)
    }

    @Test
    fun `getBmiCategory returns normal for BMI between 18 and 24`() {
        // Given
        val bmi = 22.0f

        // When
        val category = BmiUtils.getBmiCategory(bmi)

        // Then
        assertEquals("正常", category)
    }

    @Test
    fun `getBmiCategory returns overweight for BMI between 24 and 28`() {
        // Given
        val bmi = 26.0f

        // When
        val category = BmiUtils.getBmiCategory(bmi)

        // Then
        assertEquals("偏胖", category)
    }

    @Test
    fun `getBmiCategory returns obese for BMI greater than 28`() {
        // Given
        val bmi = 30.0f

        // When
        val category = BmiUtils.getBmiCategory(bmi)

        // Then
        assertEquals("肥胖", category)
    }

    @Test
    fun `getHealthyWeightRange returns correct range`() {
        // Given
        val height = 175f // cm

        // When
        val range = BmiUtils.getHealthyWeightRange(height)

        // Then
        assertNotNull(range)
        val (min, max) = range!!
        assertEquals(56.66f, min, 0.1f)
        assertEquals(73.5f, max, 0.1f)
    }

    @Test
    fun `getHealthyWeightRange with zero height returns null`() {
        // Given
        val height = 0f

        // When
        val range = BmiUtils.getHealthyWeightRange(height)

        // Then
        assertNull(range)
    }

    @Test
    fun `getHealthyWeightRange with negative height returns null`() {
        // Given
        val height = -175f

        // When
        val range = BmiUtils.getHealthyWeightRange(height)

        // Then
        assertNull(range)
    }
}
