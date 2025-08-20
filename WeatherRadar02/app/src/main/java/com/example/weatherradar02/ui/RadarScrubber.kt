package com.example.weatherradar02.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherradar02.R
import com.example.weatherradar02.model.RadarFrame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable for the radar scrubber controls
 */
@Composable
fun RadarScrubber(
    radarFrames: List<RadarFrame>,
    currentPosition: Int,
    isPlaying: Boolean,
    onPositionChange: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (radarFrames.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Time display for current frame
        if (currentPosition < radarFrames.size) {
            val currentFrame = radarFrames[currentPosition]
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            val timeString = dateFormat.format(Date(currentFrame.timestamp * 1000))

            Text(
                text = timeString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Scrubber controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play/Pause button
            IconButton(
                onClick = onTogglePlayPause
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.play_pause)
                )
            }

            // Slider for frame selection
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { onPositionChange(it.toInt()) },
                valueRange = 0f..(radarFrames.size - 1).toFloat(),
                steps = radarFrames.size - 2, // steps between min and max
                modifier = Modifier.weight(1f)
            )

            // Frame counter
            Text(
                text = "${currentPosition + 1}/${radarFrames.size}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}