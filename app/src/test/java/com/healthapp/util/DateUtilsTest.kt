package com.healthapp.util

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class DateUtilsTest {

    @Test
    fun `getToday returns date in correct format`() {
        // When
        val today = DateUtils.getToday()

        // Then
        assertNotNull(today)
        assertTrue(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `getCurrentSeason returns valid season`() {
        // When
        val season = DateUtils.getCurrentSeason()

        // Then
        assertTrue(season in listOf("spring", "summer", "autumn", "winter"))
    }

    @Test
    fun `getGreeting returns non-empty string`() {
        // When
        val greeting = DateUtils.getGreeting()

        // Then
        assertNotNull(greeting)
        assertTrue(greeting.isNotEmpty())
    }

    @Test
    fun `calculateAge with past date returns positive age`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(1990, Calendar.JANUARY, 1)
        val birthDate = calendar.timeInMillis

        // When
        val age = DateUtils.calculateAge(birthDate)

        // Then
        assertTrue(age > 0)
        assertTrue(age < 100) // Reasonable age range
    }

    @Test
    fun `calculateAge with future date returns negative age`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2030, Calendar.JANUARY, 1)
        val birthDate = calendar.timeInMillis

        // When
        val age = DateUtils.calculateAge(birthDate)

        // Then
        assertTrue(age < 0)
    }

    @Test
    fun `formatDate returns correct format`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.MARCH, 15)
        val date = calendar.time

        // When
        val formatted = DateUtils.formatDate(date)

        // Then
        assertEquals("2024-03-15", formatted)
    }

    @Test
    fun `formatDateTime returns correct format`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.MARCH, 15, 14, 30)
        val date = calendar.time

        // When
        val formatted = DateUtils.formatDateTime(date)

        // Then
        assertEquals("2024-03-15 14:30", formatted)
    }

    @Test
    fun `formatTime returns correct format`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.MARCH, 15, 14, 30)
        val date = calendar.time

        // When
        val formatted = DateUtils.formatTime(date)

        // Then
        assertEquals("14:30", formatted)
    }
}
