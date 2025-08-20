package com.example.weatherradar02.model

/**
 * Data class representing a radar frame with timestamp and tile URL
 */
data class RadarFrame(
    val timestamp: Long,
    val tileUrl: String,
    val path: String = ""
)