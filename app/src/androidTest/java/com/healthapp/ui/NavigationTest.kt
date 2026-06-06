package com.healthapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.healthapp.navigation.HealthNavHost
import com.healthapp.ui.theme.HealthAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigationBar_displaysAllTabs() {
        // Given
        composeTestRule.setContent {
            HealthAppTheme {
                HealthNavHost()
            }
        }

        // Then - verify all navigation tabs exist
        composeTestRule.onNodeWithText("首页").assertExists()
        composeTestRule.onNodeWithText("饮食").assertExists()
        composeTestRule.onNodeWithText("运动").assertExists()
        composeTestRule.onNodeWithText("心理").assertExists()
    }

    @Test
    fun navigationBar_clickDietTab_navigatesToDiet() {
        // Given
        composeTestRule.setContent {
            HealthAppTheme {
                HealthNavHost()
            }
        }

        // When
        composeTestRule.onNodeWithText("饮食").performClick()

        // Then
        composeTestRule.onNodeWithText("饮水追踪").assertExists()
    }

    @Test
    fun navigationBar_clickExerciseTab_navigatesToExercise() {
        // Given
        composeTestRule.setContent {
            HealthAppTheme {
                HealthNavHost()
            }
        }

        // When
        composeTestRule.onNodeWithText("运动").performClick()

        // Then
        composeTestRule.onNodeWithText("今日运动").assertExists()
    }

    @Test
    fun navigationBar_clickMentalTab_navigatesToMental() {
        // Given
        composeTestRule.setContent {
            HealthAppTheme {
                HealthNavHost()
            }
        }

        // When
        composeTestRule.onNodeWithText("心理").performClick()

        // Then
        composeTestRule.onNodeWithText("记录今天的心情").assertExists()
    }
}
