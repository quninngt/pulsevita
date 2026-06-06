package com.healthapp.util

import org.junit.Test
import org.junit.Assert.*

class ConstantsTest {

    @Test
    fun `water constants are valid`() {
        assertTrue(Constants.DEFAULT_WATER_GOAL > 0)
        assertTrue(Constants.WATER_SMALL_AMOUNT > 0)
        assertTrue(Constants.WATER_MEDIUM_AMOUNT > 0)
        assertTrue(Constants.WATER_LARGE_AMOUNT > 0)
        assertTrue(Constants.WATER_SMALL_AMOUNT < Constants.WATER_MEDIUM_AMOUNT)
        assertTrue(Constants.WATER_MEDIUM_AMOUNT < Constants.WATER_LARGE_AMOUNT)
    }

    @Test
    fun `exercise constants are valid`() {
        assertTrue(Constants.DEFAULT_EXERCISE_DURATION_GOAL > 0)
        assertTrue(Constants.DEFAULT_STEPS_GOAL > 0)
    }

    @Test
    fun `mood levels are in correct range`() {
        assertTrue(Constants.MOOD_VERY_BAD in 1..5)
        assertTrue(Constants.MOOD_BAD in 1..5)
        assertTrue(Constants.MOOD_NEUTRAL in 1..5)
        assertTrue(Constants.MOOD_GOOD in 1..5)
        assertTrue(Constants.MOOD_VERY_GOOD in 1..5)
        assertEquals(1, Constants.MOOD_VERY_BAD)
        assertEquals(5, Constants.MOOD_VERY_GOOD)
    }

    @Test
    fun `exercise types are valid`() {
        assertNotNull(Constants.EXERCISE_WALKING)
        assertNotNull(Constants.EXERCISE_OFFICE)
        assertNotNull(Constants.EXERCISE_YOGA)
        assertTrue(Constants.EXERCISE_WALKING.isNotEmpty())
        assertTrue(Constants.EXERCISE_OFFICE.isNotEmpty())
        assertTrue(Constants.EXERCISE_YOGA.isNotEmpty())
    }

    @Test
    fun `meal types are valid`() {
        assertNotNull(Constants.MEAL_BREAKFAST)
        assertNotNull(Constants.MEAL_LUNCH)
        assertNotNull(Constants.MEAL_DINNER)
        assertNotNull(Constants.MEAL_SNACK)
    }

    @Test
    fun `health tip categories are valid`() {
        assertNotNull(Constants.TIP_DIET)
        assertNotNull(Constants.TIP_EXERCISE)
        assertNotNull(Constants.TIP_MENTAL)
        assertNotNull(Constants.TIP_TCM)
    }

    @Test
    fun `seasons are valid`() {
        assertNotNull(Constants.SEASON_SPRING)
        assertNotNull(Constants.SEASON_SUMMER)
        assertNotNull(Constants.SEASON_AUTUMN)
        assertNotNull(Constants.SEASON_WINTER)
    }

    @Test
    fun `breathing exercise constants are valid`() {
        assertTrue(Constants.BREATHING_INHALE_SECONDS > 0)
        assertTrue(Constants.BREATHING_HOLD_SECONDS > 0)
        assertTrue(Constants.BREATHING_EXHALE_SECONDS > 0)
        assertEquals(4, Constants.BREATHING_INHALE_SECONDS)
        assertEquals(7, Constants.BREATHING_HOLD_SECONDS)
        assertEquals(8, Constants.BREATHING_EXHALE_SECONDS)
    }
}
