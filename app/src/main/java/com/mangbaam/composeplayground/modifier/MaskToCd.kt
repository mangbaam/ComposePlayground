package com.mangbaam.composeplayground.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.R
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme

fun Modifier.maskToCd(
    holeSize: (size: Size) -> Float = { it.minDimension / 8f },
    borderWidth: Dp = 2.dp,
) = graphicsLayer {
    compositingStrategy = CompositingStrategy.Offscreen
}.drawWithCache {
    val path = Path().apply {
        addOval(Rect(topLeft = Offset.Zero, bottomRight = Offset(size.width, size.height)))
    }
    val borderWidthPx = borderWidth.toPx()
    onDrawWithContent {
        clipPath(path) {
            this@onDrawWithContent.drawContent()
        }
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = size.minDimension / 2f,
            center = center,
            style = Stroke(width = borderWidthPx),
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.4f),
            radius = holeSize(size) + borderWidthPx,
            center = center,
        )
        drawCircle(
            color = Color.Black,
            radius = holeSize(size),
            center = Offset(size.width / 2, size.height / 2),
            blendMode = BlendMode.Clear,
        )
    }
}

@Preview
@Composable
private fun MaskToCdPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "angle",
    )
    ComposePlaygroundTheme {
        Surface(
            modifier = Modifier.graphicsLayer {
                rotationX = 0f
                cameraDistance = 20f
            },
            color = Color.DarkGray,
            shadowElevation = 3.dp,
        ) {
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .drawWithContent {
                        drawContent()
                        drawIntoCanvas {
                            rotate(-30f) {
                                drawRect(
                                    color = Color.Black,
                                    size = Size(30f, 400f),
                                    topLeft = Offset(size.width - 300f, size.height - 400f),
                                )
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .graphicsLayer(
                            rotationX = 0f,
                            cameraDistance = 20f,
                        )
                        .rotate(angle)
                        .maskToCd(),
                    painter = painterResource(R.drawable.chatshire),
                    contentScale = ContentScale.Crop,
                    contentDescription = "iu chatshire album",
                )
            }
        }
    }
}
