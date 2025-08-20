package com.example.weatherradar02.model

/**
 * Data class representing a geographic location
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
)