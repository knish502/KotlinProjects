package com.example.weatherradarapp

import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.weatherradarapp.ui.theme.WeatherRadarAppTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherRadarAppTheme {
                RadarScreen()
            }
        }
    }
}

class RadarViewModel : ViewModel() {
    private val _stationCode = MutableStateFlow("KHTX")
    val stationCode: StateFlow<String> = _stationCode

    fun updateStationCode(code: String) {
        viewModelScope.launch {
            _stationCode.value = code
        }
    }
}

@Composable
fun RadarScreen(
    radarViewModel: RadarViewModel = viewModel()
) {
    val stationCodeState = radarViewModel.stationCode.collectAsState()
    val stationCode = stationCodeState.value

    val radarUrl = "https://radar.weather.gov/ridge/standard/${stationCode}_loop.gif"

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(AnimatedImageDecoder.Factory())
        }
        .build()

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(radarUrl)
            .allowHardware(false)
            .build(),
        imageLoader = imageLoader,
        contentDescription = "Radar animation",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )

}