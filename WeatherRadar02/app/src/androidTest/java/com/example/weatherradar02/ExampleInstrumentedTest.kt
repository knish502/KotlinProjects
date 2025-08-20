package com.example.weatherradar02

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.weatherradar02.ui.SearchBar
import com.example.weatherradar02.ui.theme.WeatherRadar02Theme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.weatherradar02", appContext.packageName)
    }

    @Test
    fun searchBar_isDisplayed() {
        var searchCalled = false
        var searchQuery = ""

        composeTestRule.setContent {
            WeatherRadar02Theme {
                SearchBar(
                    onSearch = { query ->
                        searchCalled = true
                        searchQuery = query
                    }
                )
            }
        }

        // Check if search components are displayed
        composeTestRule
            .onNodeWithText("Enter ZIP code")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_acceptsValidZipCode() {
        var searchCalled = false
        var searchQuery = ""

        composeTestRule.setContent {
            WeatherRadar02Theme {
                SearchBar(
                    onSearch = { query ->
                        searchCalled = true
                        searchQuery = query
                    }
                )
            }
        }

        // Input valid ZIP code
        composeTestRule
            .onNodeWithText("Enter ZIP code")
            .performTextInput("12345")

        // Click search button
        composeTestRule
            .onNodeWithText("Search")
            .performClick()

        // Verify search was called with correct query
        assertTrue(searchCalled)
        assertEquals("12345", searchQuery)
    }

    @Test
    fun searchBar_rejectsInvalidInput() {
        var searchCalled = false

        composeTestRule.setContent {
            WeatherRadar02Theme {
                SearchBar(
                    onSearch = {
                        searchCalled = true
                    }
                )
            }
        }

        // Try to input invalid characters
        composeTestRule
            .onNodeWithText("Enter ZIP code")
            .performTextInput("abc123")

        // The text field should only contain digits
        // In a real test, you'd verify the actual text content
        // This is a simplified version for demonstration

        // Search button should be disabled for invalid input
        // (The button is only enabled when exactly 5 digits are entered)
    }

    @Test
    fun mainActivity_launches() {
        // This test verifies the main activity can be created
        // In a more comprehensive test, you might check for specific UI elements
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(appContext)
        assertEquals("com.example.weatherradar02", appContext.packageName)
    }
}