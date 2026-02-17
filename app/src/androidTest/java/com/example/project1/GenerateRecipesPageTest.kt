package com.example.project1

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GenerateRecipesPageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun generateRecipesPage_renders_andBackCallsCallback() {
        var backClicked = false

        val context = composeRule.activity
        val db = AppDatabase.getDatabase(context)
        val repo = UserRepository(db.userDao())

        composeRule.setContent {
            MaterialTheme {
                GenerateRecipesPage(
                    apiKey = "TEST_KEY",
                    userRepository = repo,
                    userId = 1,
                    onBack = { backClicked = true }
                )
            }
        }

        // Title exists
        composeRule.onNodeWithText("Search recipes by ingredients")
            .assertIsDisplayed()

        // Tap back icon in TopAppBar
        composeRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .performClick()

        assertTrue(backClicked)
    }

    @Test
    fun searchButton_disabledUntilIngredientAdded_thenEnabled_thenDisabledWhenRemoved() {
        val context = composeRule.activity
        val db = AppDatabase.getDatabase(context)
        val repo = UserRepository(db.userDao())

        composeRule.setContent {
            MaterialTheme {
                GenerateRecipesPage(
                    apiKey = "TEST_KEY",
                    userRepository = repo,
                    userId = 1,
                    onBack = {}
                )
            }
        }

        // Search disabled initially
        composeRule.onNodeWithTag("searchButton")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        // Type ingredient
        composeRule.onNodeWithTag("ingredientField")
            .assertIsDisplayed()
            .performTextInput("eggs")

        // Add ingredient
        composeRule.onNodeWithTag("addButton")
            .assertIsDisplayed()
            .performClick()

        // Chip appears
        composeRule.onNodeWithText("eggs")
            .assertIsDisplayed()

        // Search enabled now
        composeRule.onNodeWithTag("searchButton")
            .assertIsEnabled()

        // Remove ingredient by clicking chip
        composeRule.onNodeWithText("eggs")
            .performClick()

        // Chip gone (use count check so we don't need assertDoesNotExist)
        composeRule.onAllNodesWithText("eggs")
            .assertCountEquals(0)

        // Search disabled again
        composeRule.onNodeWithTag("searchButton")
            .assertIsNotEnabled()
    }
}
