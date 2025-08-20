package com.example.weatherradar02.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherradar02.model.Location
import com.example.weatherradar02.model.RadarFrame
import com.example.weatherradar02.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the weather radar app state using MVVM pattern
 */
class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    // Current search location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    // Available radar frames
    private val _radarFrames = MutableStateFlow<List<RadarFrame>>(emptyList())
    val radarFrames: StateFlow<List<RadarFrame>> = _radarFrames.asStateFlow()

    // Current scrubber position (index in radar frames)
    private val _scrubberPosition = MutableStateFlow(0)
    val scrubberPosition: StateFlow<Int> = _scrubberPosition.asStateFlow()

    // Animation playing state
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Animation job for play/pause functionality
    private var animationJob: Job? = null

    init {
        // Load radar frames on initialization
        loadRadarFrames()
        // Default location (approximate center of US)
        _currentLocation.value = Location(39.8283, -98.5795, "United States")
    }

    /**
     * Search for a location by ZIP code
     */
    fun searchLocation(zipCode: String) {
        if (zipCode.isBlank()) {
            _errorMessage.value = "Please enter a ZIP code"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.geocodeZipCode(zipCode)
                .onSuccess { location ->
                    _currentLocation.value = location
                }
                .onFailure { error ->
                    _errorMessage.value = "Failed to find location: ${error.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Load available radar frames
     */
    private fun loadRadarFrames() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRadarFrames().collect { frames ->
                _radarFrames.value = frames
                if (frames.isNotEmpty()) {
                    _scrubberPosition.value = frames.size - 1 // Start with latest frame
                }
                _isLoading.value = false
            }
        }
    }

    /**
     * Update scrubber position manually
     */
    fun updateScrubberPosition(position: Int) {
        if (position in 0 until _radarFrames.value.size) {
            _scrubberPosition.value = position
        }
    }

    /**
     * Toggle play/pause for radar animation
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    /**
     * Start radar frame animation
     */
    private fun startAnimation() {
        if (_radarFrames.value.isEmpty()) return

        _isPlaying.value = true
        animationJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(800) // 800ms delay between frames
                val nextPosition = (_scrubberPosition.value + 1) % _radarFrames.value.size
                _scrubberPosition.value = nextPosition
            }
        }
    }

    /**
     * Stop radar frame animation
     */
    private fun stopAnimation() {
        _isPlaying.value = false
        animationJob?.cancel()
        animationJob = null
    }

    /**
     * Get current radar frame for display
     */
    fun getCurrentRadarFrame(): RadarFrame? {
        val frames = _radarFrames.value
        val position = _scrubberPosition.value
        return if (frames.isNotEmpty() && position < frames.size) {
            frames[position]
        } else {
            null
        }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAnimation()
    }
}