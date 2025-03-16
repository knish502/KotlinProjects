package com.example.weatherradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.weatherradar.ui.theme.WeatherRadarTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherRadarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Foo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

class FooViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()

    fun incrementCount() {
        _count.value += 1
    }
}

@Composable
fun Foo(
    modifier: Modifier = Modifier,
    viewModel: FooViewModel = viewModel()
){
    val count by viewModel.count.collectAsState()
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's up, dog? Tap number $count",
            modifier = modifier
        )

        Button(
            onClick = {
                viewModel.incrementCount()
            }
        ) {
            Text(text = "Test")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FooPreview(){
    WeatherRadarTheme {
        Foo()
    }
}