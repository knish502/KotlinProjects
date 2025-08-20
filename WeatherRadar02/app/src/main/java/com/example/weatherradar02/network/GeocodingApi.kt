package com.example.weatherradar02.network

import com.example.weatherradar02.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for Google Geocoding API
 */
interface GeocodingApi {
    @GET("geocode/json")
    suspend fun geocodeZipCode(
        @Query("address") zipCode: String,
        @Query("key") apiKey: String,
        @Query("components") components: String = "country:US"
    ): Response<GeocodingResponse>
}