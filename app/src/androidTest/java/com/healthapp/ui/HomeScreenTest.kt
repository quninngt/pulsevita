package com.healthapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.healthapp.ui.home.HomeScreen
import com.healthapp.ui.theme.HealthAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysGreeting() {
        // Given
        composeTestRule.setContent {
            HealthAppTheme {
                // Note: This test requires Hilt setup for full testing
                // For now, we'll test the UI components
            }
        }

        // Then - verify basic UI elements exist
        composeTestRule.onNodeWithText("PulseVita").assertExists()
    }

    @Test
    fun homeScreen_displaysOverviewCard() {
        composeTestRule.setContent {
            HealthAppTheme {
                // Test overview card exists
            }
        }

        composeTestRule.onNodeWithText("今日概览").assertExists()
    }

    @Test
    fun homeScreen_displaysQuickActions() {
        composeTestRule.setContent {
            HealthAppTheme {
                // Test quick action buttons
            }
        }

        composeTestRule.onNodeWithText("喝水").assertExists()
        composeTestRule.onNodeWithText("运动").assertExists()
        composeTestRule.onNodeWithText("心情").assertExists()
    }
}
