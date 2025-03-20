package com.example.ai_weatherradar

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WeatherRadarApp(savedInstanceState)
            }
        }
    }
}

class WeatherViewModel : ViewModel() {
    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()

    private val _radarUrl = MutableStateFlow<String?>(null)
    val radarUrl: StateFlow<String?> = _radarUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _timestamp = MutableStateFlow<String?>(null)
    val timestamp: StateFlow<String?> = _timestamp.asStateFlow()

    private val client = OkHttpClient()

    fun updateLocation(latLng: LatLng) {
        _location.value = latLng
        fetchRadarData(latLng)
    }

    private fun fetchRadarData(latLng: LatLng) {
        _isLoading.value = true
        _errorMessage.value = null

        // Format the current time to get the latest radar image (NOAA updates every ~10 minutes)
        val timeFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
        val calendar = Calendar.getInstance()
        // Round down to the nearest 10 minutes
        val minutes = calendar.get(Calendar.MINUTE)
        val roundedMinutes = (minutes / 10) * 10
        calendar.set(Calendar.MINUTE, roundedMinutes)
        val timeString = timeFormat.format(calendar.time)

        // Build the NOAA weather radar URL
        // This URL format accesses the NOAA Ridge Radar mosaic
        val radarUrl = "https://radar.weather.gov/ridge/standard/CONUS_${timeString}_N0R.gif"

        // For demonstration, we're setting the URL directly as the API doesn't require additional processing
        _radarUrl.value = radarUrl
        _timestamp.value = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(calendar.time)
        _isLoading.value = false

        // If you need to fetch actual metadata about the radar image, you could use:
        /*
        val metadataUrl = "https://api.weather.gov/radar/stations?location=${latLng.latitude},${latLng.longitude}"
        val request = Request.Builder()
            .url(metadataUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _errorMessage.value = "Failed to load radar data: ${e.message}"
                _isLoading.value = false
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonResponse = JSONObject(response.body?.string() ?: "")
                        // Process the response to get the closest radar station
                        // Then build URL for that station's radar data
                        // _radarUrl.value = constructedUrl
                        _isLoading.value = false
                    } catch (e: Exception) {
                        _errorMessage.value = "Error parsing radar data: ${e.message}"
                        _isLoading.value = false
                    }
                } else {
                    _errorMessage.value = "Error loading radar data: ${response.code}"
                    _isLoading.value = false
                }
            }
        })
        */
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

@Composable
fun WeatherRadarApp(savedInstanceState: Bundle?) {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel()
    val location by viewModel.location.collectAsState()
    val radarUrl by viewModel.radarUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val timestamp by viewModel.timestamp.collectAsState()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission.value) {
            getLocation(fusedLocationClient) { lat, lng ->
                viewModel.updateLocation(LatLng(lat, lng))
            }
        }
    }

    LaunchedEffect(hasLocationPermission.value) {
        if (!hasLocationPermission.value) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLocation(fusedLocationClient) { lat, lng ->
                viewModel.updateLocation(LatLng(lat, lng))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (location != null) {
            WeatherRadarMap(
                location = location!!,
                radarUrl = radarUrl,
                savedInstanceState = savedInstanceState,
                modifier = Modifier.fillMaxSize()
            )

            // Display timestamp at the top
            timestamp?.let {
                Text(
                    text = "Radar data as of: $it",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Display loading indicator
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Display error messages
            errorMessage?.let {
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(it) },
                    confirmButton = {
                        Button(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                )
            }
        } else {
            // Show loading state while getting location
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Getting your location...", fontSize = 16.sp)
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Double, Double) -> Unit
) {
    try {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.locations.firstOrNull()?.let { location ->
                        onLocationReceived(location.latitude, location.longitude)
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@SuppressLint("MissingPermission")
@Composable
fun WeatherRadarMap(
    location: LatLng,
    radarUrl: String?,
    savedInstanceState: Bundle?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var radarOverlay by remember { mutableStateOf<GroundOverlay?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Remember the lifecycle callbacks for the MapView
    val lifecycleObserver = rememberMapViewLifecycleObserver(mapView, savedInstanceState)

    DisposableEffect(Unit) {
        onDispose {
            lifecycleObserver.onDispose()
        }
    }

    // Initialize the map and add the radar overlay
    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            googleMap = map
            map.isMyLocationEnabled = true
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isCompassEnabled = true
            map.uiSettings.isMapToolbarEnabled = true

            // Move camera to user's location
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    8f // Default zoom level
                )
            )
        }
    }

    // Update the radar overlay when URL changes
    LaunchedEffect(radarUrl) {
        radarUrl?.let { url ->
            googleMap?.let { map ->
                // Remove previous overlay if exists
                radarOverlay?.remove()

                // The radar image covers the entire US, so we set bounds accordingly
                val bounds = LatLngBounds(
                    LatLng(21.0, -130.0),  // Southwest corner (roughly covers all of US)
                    LatLng(50.0, -65.0)    // Northeast corner
                )

                // Add new radar overlay
                radarOverlay = map.addGroundOverlay(
                    GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromUrl(url))
                        .positionFromBounds(bounds)
                        .transparency(0.2f) // 20% transparent
                )
            }
        }
    }

    // The MapView to display
    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

// Helper class to manage MapView lifecycle
class MapViewLifecycleObserver(
    private val mapView: MapView,
    private val savedInstanceState: Bundle?
) {
    fun onCreate() {
        mapView.onCreate(savedInstanceState)
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onResume() {
        mapView.onResume()
    }

    fun onPause() {
        mapView.onPause()
    }

    fun onStop() {
        mapView.onStop()
    }

    fun onDestroy() {
        mapView.onDestroy()
    }

    fun onDispose() {
        // Clean up when the composable is disposed
        mapView.onDestroy()
    }

    fun onLowMemory() {
        mapView.onLowMemory()
    }
}

@Composable
fun rememberMapViewLifecycleObserver(
    mapView: MapView,
    savedInstanceState: Bundle?
): MapViewLifecycleObserver {
    val context = LocalContext.current
    val lifecycleObserver = remember {
        MapViewLifecycleObserver(mapView, savedInstanceState)
    }

    // Handle the lifecycle events
    DisposableEffect(context) {
        lifecycleObserver.onCreate()
        onDispose {}
    }

    DisposableEffect(Unit) {
        lifecycleObserver.onStart()
        lifecycleObserver.onResume()
        onDispose {
            lifecycleObserver.onPause()
            lifecycleObserver.onStop()
        }
    }

    return lifecycleObserver
}