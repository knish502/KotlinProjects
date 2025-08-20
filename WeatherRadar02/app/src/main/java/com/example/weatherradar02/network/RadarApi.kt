package com.example.weatherradar02.network

import retrofit2.Response
import retrofit2.http.GET

/**
 * Interface for radar data - using RainViewer API as example
 */
interface RadarApi {
    @GET("public/weather-maps.json")
    suspend fun getRadarFrames(): Response<RadarApiResponse>
}

data class RadarApiResponse(
    val version: String,
    val generated: Long,
    val host: String,
    val radar: RadarData
)

data class RadarData(
    val past: List<RadarTimeFrame>,
    val nowcast: List<RadarTimeFrame>
)

data class RadarTimeFrame(
    val time: Long,
    val path: String
)