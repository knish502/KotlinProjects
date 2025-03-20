package com.example.ai_guessinggame

// Android imports for activity and UI elements
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

// MainActivity serves as the entry point of the Android app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Sets the content of the activity to the NumberGuessingGame Composable function
            NumberGuessingGame()
        }
    }
}

@Composable
fun NumberGuessingGame() {
    // Generates a random target number between 1 and 1000
    var targetNumber by remember { mutableStateOf(Random.nextInt(1, 1001)) }

    // Stores the user's input as text
    var userInput by remember { mutableStateOf(TextFieldValue("")) }

    // Stores the message displayed to the user (e.g., feedback after a guess)
    var message by remember { mutableStateOf("Guess a number between 1 and 1000!") }

    // Stores the last guessed number
    var lastGuess by remember { mutableStateOf<Int?>(null) }

    // Keeps track of the number of attempts
    var attempts by remember { mutableStateOf(0) }

    // Controls whether the confetti animation is shown
    var showConfetti by remember { mutableStateOf(false) }

    // Tracks whether the user's input is valid
    var isValidInput by remember { mutableStateOf(true) }

    // Creates a coroutine scope to manage animations and delays
    val coroutineScope = rememberCoroutineScope()

    // Automatically hides the confetti animation after 3 seconds
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(3000)
            showConfetti = false
        }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title text
        Text(text = "Number Guessing Game", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for the user's guess
        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
                // Validate input: should be a number between 1 and 1000
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

        // Submit button to check the user's guess
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
                userInput = TextFieldValue("") // Clears the input field after submission
            },
            enabled = isValidInput && userInput.text.isNotEmpty()
        ) {
            Text("Submit Guess")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reset button to restart the game
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

        // Displays the number of attempts made
        Text(text = "Attempts: $attempts", style = MaterialTheme.typography.bodyMedium)

        // Displays the last guess if one has been made
        lastGuess?.let {
            Text(text = "Last Guess: $it", style = MaterialTheme.typography.bodyMedium)
        }

        // Animated confetti effect when the player wins
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

        // Displays feedback messages (e.g., "Too high!", "Too low!", "Correct!")
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (message.contains("Correct!")) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

// Confetti animation for when the player wins
@Composable
fun ConfettiAnimation() {
    // Represents an animated confetti particle
    class AnimatedParticle {
        var x by mutableStateOf(0f)
        var y by mutableStateOf(0f)
        val color = listOf(
            Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
        ).random()
        val size = Random.nextFloat() * 10f + 5f
        var speed = Random.nextFloat() * 5f + 3f
    }

    // Generates a list of 50 particles with random starting positions
    val particles = remember {
        List(50) {
            AnimatedParticle().also { particle ->
                particle.x = Random.nextFloat() * 300f
                particle.y = Random.nextFloat() * -200f  // Start above the screen
            }
        }
    }

    // Animation logic
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    // Animate each particle
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // Approximate 60fps
            particles.forEach { particle ->
                // Move particles downward with a slight horizontal shift
                particle.y += particle.speed
                particle.x += (Random.nextFloat() - 0.5f) * 3f

                // Reset particles that move out of view
                if (particle.y > 600f) {
                    particle.y = Random.nextFloat() * -100f
                    particle.x = Random.nextFloat() * 300f
                }
            }
        }
    }

    // Draws the confetti particles on the screen
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

// Preview function to show the UI in Android Studio
@Preview(showBackground = true)
@Composable
fun PreviewGame() {
    NumberGuessingGame()
}
