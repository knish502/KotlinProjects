package com.example.weatherradar02

import com.example.weatherradar02.model.Location
import com.example.weatherradar02.model.RadarFrame
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit tests, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun location_creation_isCorrect() {
        val location = Location(
            latitude = 40.7128,
            longitude = -74.0060,
            name = "New York, NY"
        )

        assertEquals(40.7128, location.latitude, 0.0001)
        assertEquals(-74.0060, location.longitude, 0.0001)
        assertEquals("New York, NY", location.name)
    }

    @Test
    fun location_default_name_isEmpty() {
        val location = Location(
            latitude = 34.0522,
            longitude = -118.2437
        )

        assertEquals("", location.name)
        assertEquals(34.0522, location.latitude, 0.0001)
        assertEquals(-118.2437, location.longitude, 0.0001)
    }

    @Test
    fun radarFrame_creation_isCorrect() {
        val timestamp = 1640995200L // January 1, 2022 00:00:00 GMT
        val tileUrl = "https://example.com/radar/{z}/{x}/{y}.png"
        val path = "/v2/coverage/0/2022_01_01_00_00"

        val radarFrame = RadarFrame(
            timestamp = timestamp,
            tileUrl = tileUrl,
            path = path
        )

        assertEquals(timestamp, radarFrame.timestamp)
        assertEquals(tileUrl, radarFrame.tileUrl)
        assertEquals(path, radarFrame.path)
    }

    @Test
    fun radarFrame_default_path_isEmpty() {
        val radarFrame = RadarFrame(
            timestamp = 1640995200L,
            tileUrl = "https://example.com/tile.png"
        )

        assertEquals("", radarFrame.path)
        assertEquals(1640995200L, radarFrame.timestamp)
        assertEquals("https://example.com/tile.png", radarFrame.tileUrl)
    }

    @Test
    fun zipCode_validation_logic() {
        // Test ZIP code validation logic (5 digits only)
        val validZipCodes = listOf("12345", "90210", "10001", "33101")
        val invalidZipCodes = listOf("1234", "123456", "abcde", "1234a", "")

        validZipCodes.forEach { zipCode ->
            assertTrue("$zipCode should be valid", isValidZipCode(zipCode))
        }

        invalidZipCodes.forEach { zipCode ->
            assertFalse("$zipCode should be invalid", isValidZipCode(zipCode))
        }
    }

    @Test
    fun tileUrl_placeholder_replacement() {
        val templateUrl = "https://tilecache.rainviewer.com/v2/radar/1640995200/256/{z}/{x}/{y}/2/1_1.png"
        val expectedUrl = "https://tilecache.rainviewer.com/v2/radar/1640995200/256/10/512/256/2/1_1.png"

        val actualUrl = templateUrl
            .replace("{z}", "10")
            .replace("{x}", "512")
            .replace("{y}", "256")

        assertEquals(expectedUrl, actualUrl)
    }

    @Test
    fun radarFrame_sorting_byTimestamp() {
        val frame1 = RadarFrame(1640995200L, "url1") // Earlier
        val frame2 = RadarFrame(1640995800L, "url2") // Later
        val frame3 = RadarFrame(1640995500L, "url3") // Middle

        val unsortedFrames = listOf(frame2, frame1, frame3)
        val sortedFrames = unsortedFrames.sortedBy { it.timestamp }

        assertEquals(frame1, sortedFrames[0])
        assertEquals(frame3, sortedFrames[1])
        assertEquals(frame2, sortedFrames[2])
    }

    /**
     * Helper function to validate ZIP code format
     * In a real app, this might be in a utility class
     */
    private fun isValidZipCode(zipCode: String): Boolean {
        return zipCode.length == 5 && zipCode.all { it.isDigit() }
    }
}