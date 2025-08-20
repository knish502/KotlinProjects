package com.example.weatherradar02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherradar02.ui.MapScreen
import com.example.weatherradar02.ui.theme.WeatherRadar02Theme
import com.example.weatherradar02.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherRadar02Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherRadarApp()
                }
            }
        }
    }
}

@Composable
fun WeatherRadarApp() {
    val viewModel: WeatherViewModel = viewModel()
    MapScreen(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun WeatherRadarAppPreview() {
    WeatherRadar02Theme {
        WeatherRadarApp()
    }
}