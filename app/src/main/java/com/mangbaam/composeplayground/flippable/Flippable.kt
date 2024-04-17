package com.mangbaam.composeplayground.flippable

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme

@Composable
fun Modifier.flippable(
    maxRotationX: Float = 50f,
    maxRotationY: Float = 50f,
    @FloatRange(0.1, 1.0) speed: Float = 0.1f,
): Modifier {
    var rotationXDegrees by rememberSaveable { mutableFloatStateOf(0f) }
    var rotationYDegrees by rememberSaveable { mutableFloatStateOf(0f) }
    val rotationX by animateFloatAsState(
        targetValue = rotationXDegrees,
        animationSpec = tween(durationMillis = 30),
        label = "rotationX",
    )
    val rotationY by animateFloatAsState(
        targetValue = rotationYDegrees,
        animationSpec = tween(durationMillis = 30),
        label = "rotationY",
    )
    return this then Modifier
        .graphicsLayer {
            this.rotationX = rotationX
            this.rotationY = rotationY
            cameraDistance = 12 * density
        }
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                rotationXDegrees =
                    (rotationXDegrees - dragAmount.y * speed).coerceIn(-maxRotationY, maxRotationY)
                rotationYDegrees =
                    (rotationYDegrees + dragAmount.x * speed).coerceIn(-maxRotationX, maxRotationX)
            }
        }
}

@Preview
@Composable
private fun FlippablePreview() {
    ComposePlaygroundTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .size(300.dp)
                        .aspectRatio(1f)
                        .flippable(
                            maxRotationX = 60f,
                            maxRotationY = 60f,
                            speed = 0.3f,
                        ),
                    shape = CircleShape,
                    shadowElevation = 5.dp,
                    border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray)
                            .padding(8.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = CircleShape,
                            )
                            .padding(30.dp),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "500",
                            style = MaterialTheme.typography.displayLarge.copy(
                                letterSpacing = 10.sp,
                                shadow = Shadow(
                                    offset = Offset(5f, 10f),
                                    blurRadius = 10f,
                                ),
                            ),
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}
