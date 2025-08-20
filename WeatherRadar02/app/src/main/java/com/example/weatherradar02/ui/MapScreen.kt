package com.example.weatherradar02.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherradar02.viewmodel.WeatherViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import java.net.URL

/**
 * Main screen composable containing the map, search, and scrubber
 */
@Composable
fun MapScreen(
    viewModel: WeatherViewModel
) {
    // Collect state from ViewModel
    val currentLocation by viewModel.currentLocation.collectAsState()
    val radarFrames by viewModel.radarFrames.collectAsState()
    val scrubberPosition by viewModel.scrubberPosition.collectAsState()
    val isPlaying by viewModel.isPlaying
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Camera state for map
    val cameraPositionState = rememberCameraPositionState()

    // Update camera when location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(location.latitude, location.longitude),
                10f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar at the top
            SearchBar(
                onSearch = { zipCode ->
                    viewModel.searchLocation(zipCode)
                }
            )

            // Map with radar overlay
            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true
                    )
                ) {
                    // Location marker
                    currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(location.latitude, location.longitude)
                            ),
                            title = location.name,
                            snippet = "ZIP Code Location"
                        )
                    }

                    // Radar overlay
                    val currentFrame = viewModel.getCurrentRadarFrame()
                    if (currentFrame != null && radarFrames.isNotEmpty()) {
                        TileOverlay(
                            tileProvider = object : UrlTileProvider(256, 256) {
                                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                                    return try {
                                        // Replace placeholders in the tile URL
                                        val tileUrl = currentFrame.tileUrl
                                            .replace("{z}", zoom.toString())
                                            .replace("{x}", x.toString())
                                            .replace("{y}", y.toString())
                                        URL(tileUrl)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                            },
                            transparency = 0.3f // Semi-transparent overlay
                        )
                    }
                }

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Radar scrubber at the bottom
            RadarScrubber(
                radarFrames = radarFrames,
                currentPosition = scrubberPosition,
                isPlaying = isPlaying,
                onPositionChange = { position ->
                    viewModel.updateScrubberPosition(position)
                },
                onTogglePlayPause = {
                    viewModel.togglePlayPause()
                }
            )
        }

        // Error snackbar
        errorMessage?.let { message ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(message)
            }
        }
    }
}
