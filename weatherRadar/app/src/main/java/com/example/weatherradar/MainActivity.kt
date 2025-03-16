package com.example.weatherradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherradar.ui.theme.WeatherRadarTheme

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

@Composable
fun Foo(modifier: Modifier = Modifier){
    Text(
        text = "What's up, dog?",
        modifier = modifier
    )
}
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}

@Preview(showBackground = true)
@Composable
fun FooPreview(){
    WeatherRadarTheme {
        Foo()
    }
}
//fun GreetingPreview() {
//    WeatherRadarTheme {
//        Greeting("Android")
//    }
//}