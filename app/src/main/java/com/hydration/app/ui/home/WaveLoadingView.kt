package com.hydration.app.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun WaveLoadingView(
    currentMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (goalMl > 0) (currentMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val waveColor = MaterialTheme.colorScheme.primary
    val waveColorSecondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val radius = width / 2f

            // Create circular clipping path
            val circlePath = Path().apply {
                addCircle(width / 2f, height / 2f, radius)
            }

            clipPath(circlePath) {
                // Background of circle
                drawRect(color = waveColor.copy(alpha = 0.1f))

                // Water fill height from bottom
                val waterHeight = height * (1f - percentage)

                // Draw wave path
                val wavePath = Path().apply {
                    moveTo(0f, height)
                    val waveAmplitude = 12f
                    val waveLength = width
                    val frequency = 2f * Math.PI.toFloat() / waveLength

                    var x = 0f
                    while (x <= width) {
                        val y = waterHeight + waveAmplitude * sin(frequency * x + wavePhase)
                        if (x == 0f) moveTo(x, y) else lineTo(x, y)
                        x += 5f
                    }
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(waveColorSecondary, waveColor),
                        startY = waterHeight,
                        endY = height
                    )
                )
            }
        }

        // Center text showing progress
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$currentMl ml",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Goal: $goalMl ml",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            val percentInt = (percentage * 100).toInt()
            Text(
                text = "$percentInt%",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
