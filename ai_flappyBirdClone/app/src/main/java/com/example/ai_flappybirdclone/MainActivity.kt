package com.example.ai_flappybirdclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlappyBirdGame()
        }
    }
}

@Composable
fun FlappyBirdGame() {
    val density = LocalDensity.current
    val screenWidth = with(density) { 360.dp.toPx() }
    val screenHeight = with(density) { 640.dp.toPx() }

    var gameState by remember { mutableStateOf(GameState.WAITING) }
    var birdY by remember { mutableStateOf(screenHeight / 2) }
    var birdVelocity by remember { mutableStateOf(0f) }
    var score by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(0) }
    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }

    val coroutineScope = rememberCoroutineScope()

    // Constants
    val birdRadius = 20f
    val gravity = 0.4f
    val jumpStrength = -8f
    val obstacleWidth = 70f
    val obstacleGap = 150f
    val obstacleSpeed = 3f

    // Game loop
    LaunchedEffect(gameState) {
        while (gameState == GameState.PLAYING) {
            delay(16) // ~60 FPS

            // Update bird position
            birdVelocity += gravity
            birdVelocity = min(birdVelocity, 12f) // Terminal velocity
            birdY += birdVelocity

            // Check screen boundaries
            if (birdY - birdRadius < 0 || birdY + birdRadius > screenHeight) {
                gameState = GameState.GAME_OVER
                highScore = max(highScore, score)
            }

            // Update obstacles
            obstacles = obstacles.map { obstacle ->
                obstacle.copy(x = obstacle.x - obstacleSpeed)
            }.filter { it.x + obstacleWidth > 0 }

            // Generate new obstacles
            if (obstacles.isEmpty() || obstacles.last().x < screenWidth - 200) {
                val gapPosition = Random.nextFloat() * (screenHeight - obstacleGap - 100) + 50
                obstacles = obstacles + Obstacle(
                    x = screenWidth,
                    gapY = gapPosition,
                    gapHeight = obstacleGap,
                    passed = false
                )
            }

            // Check for collisions
            obstacles.forEach { obstacle ->
                // Check if bird has passed the obstacle
                if (!obstacle.passed && obstacle.x + obstacleWidth < screenWidth / 3) {
                    score++
                    obstacles = obstacles.map {
                        if (it == obstacle) it.copy(passed = true) else it
                    }
                }

                // Check for collision
                val birdRect = Rect(
                    left = screenWidth / 3 - birdRadius,
                    top = birdY - birdRadius,
                    right = screenWidth / 3 + birdRadius,
                    bottom = birdY + birdRadius
                )

                val topObstacleRect = Rect(
                    left = obstacle.x,
                    top = 0f,
                    right = obstacle.x + obstacleWidth,
                    bottom = obstacle.gapY
                )

                val bottomObstacleRect = Rect(
                    left = obstacle.x,
                    top = obstacle.gapY + obstacleGap,
                    right = obstacle.x + obstacleWidth,
                    bottom = screenHeight
                )

                if (birdRect.overlaps(topObstacleRect) || birdRect.overlaps(bottomObstacleRect)) {
                    gameState = GameState.GAME_OVER
                    highScore = max(highScore, score)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Sky blue background
            .pointerInput(Unit) {
                detectTapGestures {
                    when (gameState) {
                        GameState.WAITING -> {
                            gameState = GameState.PLAYING
                            birdY = screenHeight / 2
                            birdVelocity = 0f
                            score = 0
                            obstacles = emptyList()
                        }
                        GameState.PLAYING -> {
                            birdVelocity = jumpStrength
                        }
                        GameState.GAME_OVER -> {
                            gameState = GameState.WAITING
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw ground
            drawRect(
                color = Color(0xFF8B4513), // Brown ground
                topLeft = Offset(0f, screenHeight - 50),
                size = Size(screenWidth, 50f)
            )

            // Draw grass
            drawRect(
                color = Color(0xFF33AA33), // Green grass
                topLeft = Offset(0f, screenHeight - 55),
                size = Size(screenWidth, 10f)
            )

            // Draw obstacles
            obstacles.forEach { obstacle ->
                drawObstacle(
                    obstacle = obstacle,
                    width = obstacleWidth,
                    height = screenHeight,
                    color = Color(0xFF33AA33) // Green pipes
                )
            }

            // Draw bird
            drawBird(
                position = Offset(screenWidth / 3, birdY),
                radius = birdRadius,
                rotation = birdVelocity * 4 // Rotate based on velocity
            )
        }

        // Game state messages
        when (gameState) {
            GameState.WAITING -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Flappy Bird Clone",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Tap to start",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                    if (highScore > 0) {
                        Text(
                            text = "High Score: $highScore",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            GameState.GAME_OVER -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Game Over",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Score: $score",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "High Score: $highScore",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Tap to restart",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                }
            }
            else -> {
                // Draw score during gameplay
                Text(
                    text = "Score: $score",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawBird(position: Offset, radius: Float, rotation: Float) {
    // Bird body
    drawCircle(
        color = Color.Yellow,
        radius = radius,
        center = position
    )

    // Bird wing
    val wingPath = Path().apply {
        moveTo(position.x - radius * 0.8f, position.y)
        lineTo(position.x - radius * 1.5f, position.y - radius * 0.5f * (1 + (rotation / 20f).coerceIn(-1f, 1f)))
        lineTo(position.x - radius * 1.5f, position.y + radius * 0.5f)
        close()
    }
    drawPath(
        path = wingPath,
        color = Color(0xFFFFAA00) // Orange wing
    )

    // Bird eye
    drawCircle(
        color = Color.White,
        radius = radius * 0.3f,
        center = Offset(position.x + radius * 0.5f, position.y - radius * 0.3f)
    )
    drawCircle(
        color = Color.Black,
        radius = radius * 0.15f,
        center = Offset(position.x + radius * 0.6f, position.y - radius * 0.3f)
    )

    // Bird beak
    val beakPath = Path().apply {
        moveTo(position.x + radius * 0.8f, position.y)
        lineTo(position.x + radius * 1.5f, position.y - radius * 0.2f)
        lineTo(position.x + radius * 1.5f, position.y + radius * 0.2f)
        close()
    }
    drawPath(
        path = beakPath,
        color = Color.Red
    )
}

private fun DrawScope.drawObstacle(obstacle: Obstacle, width: Float, height: Float, color: Color) {
    // Top pipe
    drawRect(
        color = color,
        topLeft = Offset(obstacle.x, 0f),
        size = Size(width, obstacle.gapY)
    )
    drawRect(
        color = Color(0xFF008800), // Darker green for pipe border
        topLeft = Offset(obstacle.x, obstacle.gapY - 20),
        size = Size(width, 20f)
    )
    drawRect(
        color = color,
        topLeft = Offset(obstacle.x - 10, obstacle.gapY - 20),
        size = Size(width + 20, 20f)
    )

    // Bottom pipe
    val bottomPipeY = obstacle.gapY + obstacle.gapHeight
    drawRect(
        color = color,
        topLeft = Offset(obstacle.x, bottomPipeY),
        size = Size(width, height - bottomPipeY)
    )
    drawRect(
        color = Color(0xFF008800), // Darker green for pipe border
        topLeft = Offset(obstacle.x, bottomPipeY),
        size = Size(width, 20f)
    )
    drawRect(
        color = color,
        topLeft = Offset(obstacle.x - 10, bottomPipeY),
        size = Size(width + 20, 20f)
    )
}

data class Obstacle(
    val x: Float,
    val gapY: Float,
    val gapHeight: Float,
    val passed: Boolean
)

enum class GameState {
    WAITING, PLAYING, GAME_OVER
}

// Extension function to check if two rectangles overlap
fun Rect.overlaps(other: Rect): Boolean {
    return !(right < other.left || left > other.right || bottom < other.top || top > other.bottom)
}