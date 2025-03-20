package com.example.ai_guessinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumberGuessingGame()
        }
    }
}

@Composable
fun NumberGuessingGame() {
    var targetNumber by remember { mutableStateOf(Random.nextInt(1, 1001)) }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf("Guess a number between 1 and 1000!") }
    var lastGuess by remember { mutableStateOf<Int?>(null) }
    var attempts by remember { mutableStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }
    var isValidInput by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    // Auto-hide confetti after 3 seconds
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(3000)
            showConfetti = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Number Guessing Game", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
                isValidInput = it.text.isEmpty() ||
                        (it.text.toIntOrNull() != null && it.text.toIntOrNull()!! in 1..1000)
            },
            label = { Text("Enter your guess (1-1000)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            isError = !isValidInput,
            supportingText = {
                if (!isValidInput) {
                    Text("Please enter a number between 1 and 1000")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val guess = userInput.text.toIntOrNull()
                if (guess == null) {
                    message = "Please enter a valid number!"
                } else if (guess !in 1..1000) {
                    message = "Please enter a number between 1 and 1000!"
                } else {
                    lastGuess = guess
                    attempts++
                    message = when {
                        guess == targetNumber -> {
                            showConfetti = true
                            "🎉 Correct! You guessed the number in $attempts attempts!"
                        }
                        guess in (targetNumber - 10)..(targetNumber + 10) ->
                            if (guess < targetNumber) "Very close, but too low!"
                            else "Very close, but too high!"
                        guess < targetNumber -> "Too low! Try again."
                        else -> "Too high! Try again."
                    }
                }
                userInput = TextFieldValue("") // Clear input field after submission
            },
            enabled = isValidInput && userInput.text.isNotEmpty()
        ) {
            Text("Submit Guess")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            targetNumber = Random.nextInt(1, 1001)
            userInput = TextFieldValue("")
            message = "Guess a number between 1 and 1000!"
            attempts = 0
            lastGuess = null
            showConfetti = false
            isValidInput = true
        }) {
            Text("Reset Game")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Attempts: $attempts", style = MaterialTheme.typography.bodyMedium)

        lastGuess?.let {
            Text(text = "Last Guess: $it", style = MaterialTheme.typography.bodyMedium)
        }

        AnimatedVisibility(
            visible = showConfetti,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎊🎊 Congratulations! 🎊🎊",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(modifier = Modifier.height(200.dp)) {
                    ConfettiAnimation()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (message.contains("Correct!")) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@Composable
fun ConfettiAnimation() {
    // Create mutable state for each particle
    class AnimatedParticle {
        var x by mutableStateOf(0f)
        var y by mutableStateOf(0f)
        val color = listOf(
            Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
        ).random()
        val size = Random.nextFloat() * 10f + 5f
        var speed = Random.nextFloat() * 5f + 3f
    }

    // Create particles with random positions
    val particles = remember {
        List(50) {
            AnimatedParticle().also { particle ->
                particle.x = Random.nextFloat() * 300f
                particle.y = Random.nextFloat() * -200f  // Start above the visible area
            }
        }
    }

    // Animation logic
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    // Animate each particle
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // Roughly 60fps
            particles.forEach { particle ->
                // Move particles downward with some horizontal movement
                particle.y += particle.speed
                particle.x += (Random.nextFloat() - 0.5f) * 3f

                // Reset particles that fall out of view
                if (particle.y > 600f) {
                    particle.y = Random.nextFloat() * -100f
                    particle.x = Random.nextFloat() * 300f
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(particle.x, particle.y)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGame() {
    NumberGuessingGame()
}