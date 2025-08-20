package com.example.weatherradar02.model

import com.google.gson.annotations.SerializedName

/**
 * Data classes for Google Geocoding API response
 */
data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val geometry: Geometry,
    @SerializedName("formatted_address")
    val formattedAddress: String
)

data class Geometry(
    val location: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)