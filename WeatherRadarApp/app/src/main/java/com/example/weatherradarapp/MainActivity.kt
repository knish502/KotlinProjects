package com.example.weatherradarapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder

/**
 * MainActivity is the entry point for our Android app.
 * It sets up a simple UI that displays a weather radar GIF.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is created.
     * We use setContent to define the UI using Jetpack Compose.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content of this activity to our composable function, RadarScreen.
        setContent {
            RadarScreen()
        }
    }
}

/**
 * RadarScreen is a composable function that displays a looping radar GIF.
 */
@Composable
fun RadarScreen() {
    // Construct a java.net.URL, then convert it to a String for Coil.
    // NOAA’s weather radar GIF URL for KHTX.
    val javaUrl: java.net.URL = java.net.URL("https://radar.weather.gov/ridge/standard/KHTX_loop.gif")
    val imageUrl = javaUrl.toString()

    // LocalContext gives us the current Context in a composable environment.
    val context = LocalContext.current

    // Create a custom ImageLoader to enable decoding animated GIFs via ImageDecoderDecoder.
    val imageLoader = ImageLoader.Builder(context)
        .components {
            // ImageDecoderDecoder.Factory is for API 28+, giving better
            // animated GIF (and possibly WebP) support.
            add(ImageDecoderDecoder.Factory())
        }
        .build()

    // Log the type of imageUrl to confirm we’re passing a String (not java.net.URL).
    Log.d("ModelCheck", "imageUrl type: ${imageUrl::class.java.name}")

    // Box arranges its children on top of each other.
    // We use fillMaxSize() to occupy the entire screen,
    // and contentAlignment to center the child (our AsyncImage).
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // AsyncImage is Coil's composable for loading and displaying images (including GIFs).
        AsyncImage(
            model = imageUrl,                  // The URL string to load
            contentDescription = "Radar Image", // Accessibility description
            imageLoader = imageLoader,          // Use our custom ImageLoader for GIF support

            // These optional callbacks help with debugging or handling different states.
            onError = {
                Log.e("error", "Coil Error -> ${it.result.throwable}")
            },
            onSuccess = {
                Log.i("info", "Coil Success -> Image loaded.")
            },
            onLoading = {
                Log.i("info", "Coil loading...")
            }
        )
    }
}
