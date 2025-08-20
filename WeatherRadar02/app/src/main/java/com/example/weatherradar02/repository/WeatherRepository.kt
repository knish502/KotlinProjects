package com.example.weatherradar02.repository

import com.example.weatherradar02.model.Location
import com.example.weatherradar02.model.RadarFrame
import com.example.weatherradar02.network.GeocodingApi
import com.example.weatherradar02.network.RadarApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository class handling all data operations for weather and location services
 */
class WeatherRepository {

    // TODO: Replace with your actual API keys
    private val googleApiKey = "AIzaSyB1afEKH3_6hG1e7PGv2b2XVa_UBUuWfT8"

    private val geocodingApi: GeocodingApi = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeocodingApi::class.java)

    private val radarApi: RadarApi = Retrofit.Builder()
        .baseUrl("https://api.rainviewer.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RadarApi::class.java)

    /**
     * Geocode a ZIP code to get latitude/longitude coordinates
     */
    suspend fun geocodeZipCode(zipCode: String): Result<Location> {
        return try {
            val response = geocodingApi.geocodeZipCode(zipCode, googleApiKey)
            if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                val result = response.body()!!.results[0]
                val location = Location(
                    latitude = result.geometry.location.lat,
                    longitude = result.geometry.location.lng,
                    name = result.formattedAddress
                )
                Result.success(location)
            } else {
                Result.failure(Exception("Geocoding failed: ${response.body()?.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch available radar frames from RainViewer API
     */
    suspend fun getRadarFrames(): Flow<List<RadarFrame>> = flow {
        try {
            val response = radarApi.getRadarFrames()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                val frames = mutableListOf<RadarFrame>()

                // Add past frames
                apiResponse.radar.past.forEach { frame ->
                    frames.add(
                        RadarFrame(
                            timestamp = frame.time,
                            tileUrl = "https://tilecache.rainviewer.com${frame.path}/256/{z}/{x}/{y}/2/1_1.png",
                            path = frame.path
                        )
                    )
                }

                // Add nowcast frames
                apiResponse.radar.nowcast.forEach { frame ->
                    frames.add(
                        RadarFrame(
                            timestamp = frame.time,
                            tileUrl = "https://tilecache.rainviewer.com${frame.path}/256/{z}/{x}/{y}/2/1_1.png",
                            path = frame.path
                        )
                    )
                }

                emit(frames.sortedBy { it.timestamp })
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}