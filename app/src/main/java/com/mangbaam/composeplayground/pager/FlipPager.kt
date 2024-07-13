package com.mangbaam.composeplayground.pager

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mangbaam.composeplayground.extension.endOffsetForPage
import com.mangbaam.composeplayground.extension.offsetForPage
import com.mangbaam.composeplayground.extension.startOffsetForPage
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun FlipPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    orientation: Orientation = Horizontal,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val overScrollAmount = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.isScrollInProgress }.collect {
            if (!it) overScrollAmount.floatValue = 0f
        }
    }
    val animatedOverScrollAmount by animateFloatAsState(
        targetValue = overScrollAmount.floatValue / 500,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "animatedOverScrollAmount",
    )
    val nestedScrollConnection = rememberFlipPagerOverScroll(
        orientation = orientation,
        overScrollAmount = overScrollAmount,
    )

    when (orientation) {
        Vertical -> {
            VerticalPager(
                state = state,
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
                pageContent = {
                    Content(
                        it, state, orientation, pageContent, animatedOverScrollAmount,
                    )
                },
            )
        }

        Horizontal -> {
            HorizontalPager(
                state = state,
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
                pageContent = {
                    Content(
                        it, state, orientation, pageContent, animatedOverScrollAmount,
                    )
                },
            )
        }
    }
}

@Composable
private fun Content(
    page: Int,
    state: PagerState,
    orientation: Orientation,
    pageContent: @Composable (page: Int) -> Unit,
    animatedOverScrollAmount: Float,
) {
    var zIndex by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.offsetForPage(page) }.collect {
            zIndex = when (state.offsetForPage(page)) {
                in -0.5f..(0.5f) -> 3f
                in -1f..1f -> 2f
                else -> 1f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .graphicsLayer {
                val pageOffset = state.offsetForPage(page)
                when (orientation) {
                    Vertical -> translationY = size.height * pageOffset
                    Horizontal -> translationX = size.width * pageOffset
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        val graphicsLayer = rememberGraphicsLayer()
        val isImageBitmapNull by remember {
            derivedStateOf { imageBitmap == null }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .alpha(if (state.isScrollInProgress && !isImageBitmapNull) 0f else 1f)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                },
            contentAlignment = Alignment.Center,
        ) {
            pageContent(page)
        }

        LaunchedEffect(state.isScrollInProgress) {
            while (true) {
                if (graphicsLayer.size.width != 0) imageBitmap = graphicsLayer.toImageBitmap()
                delay(if (state.isScrollInProgress) 16 else 300)
            }
        }
        LaunchedEffect(MaterialTheme.colorScheme.surface) {
            if (graphicsLayer.size.width != 0) imageBitmap = graphicsLayer.toImageBitmap()
        }

        PageFlip(
            modifier = Modifier.fillMaxSize(),
            shape = when (orientation) {
                Vertical -> FlipShape.Top
                Horizontal -> FlipShape.Left
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverScrollAmount = { animatedOverScrollAmount },
        )
        PageFlip(
            modifier = Modifier.fillMaxSize(),
            shape = when (orientation) {
                Vertical -> FlipShape.Bottom
                Horizontal -> FlipShape.Right
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverScrollAmount = { animatedOverScrollAmount },
        )
    }
}

@Composable
private fun BoxScope.PageFlip(
    modifier: Modifier = Modifier,
    shape: FlipShape,
    imageBitmap: () -> ImageBitmap?,
    state: PagerState,
    page: Int,
    animatedOverScrollAmount: () -> Float = { 0f },
) {
    val size by remember {
        derivedStateOf {
            imageBitmap()?.let {
                DpSize(it.width.dp, it.height.dp)
            } ?: DpSize.Zero
        }
    }
    Canvas(
        modifier = modifier
            .size(size)
            .align(Alignment.TopStart)
            .graphicsLayer {
                this.shape = shape
                clip = true

                cameraDistance = 65f
                when (shape) {
                    FlipShape.Top -> {
                        rotationX = minOf(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverScrollAmount().coerceAtLeast(0f) * -20f,
                        )
                    }

                    FlipShape.Bottom -> {
                        rotationX = maxOf(
                            (-state.offsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverScrollAmount().coerceAtMost(0f) * -20f,
                        )
                    }

                    FlipShape.Left -> {
                        rotationY = -minOf(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverScrollAmount().coerceAtLeast(0f) * -20f,
                        )
                    }

                    FlipShape.Right -> {
                        rotationY = -maxOf(
                            (state.endOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverScrollAmount().coerceAtMost(0f) * -20f,
                        )
                    }
                }
            },
    ) {
        imageBitmap()?.let { imageBitmap ->
            drawImage(imageBitmap)
            drawImage(
                imageBitmap,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(
                        alpha = when (shape) {
                            FlipShape.Top, FlipShape.Left -> maxOf(
                                (state.endOffsetForPage(page).absoluteValue * 0.9f.coerceIn(0f..1f)),
                                animatedOverScrollAmount() * 0.3f,
                            )

                            FlipShape.Bottom, FlipShape.Right -> maxOf(
                                (state.startOffsetForPage(page) * 0.9f.coerceIn(0f..1f)),
                                -animatedOverScrollAmount() * 0.3f
                            )
                        }
                    )
                )
            )
        }
    }
}

@Composable
private fun rememberFlipPagerOverScroll(
    orientation: Orientation,
    overScrollAmount: MutableFloatState,
): NestedScrollConnection {
    val nestedScrollConnection = remember(orientation) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (overScrollAmount.floatValue != 0f) {
                    when (orientation) {
                        Vertical -> calculateOverScroll(available.y)
                        Horizontal -> calculateOverScroll(available.x)
                    }
                    return available
                }

                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                when (orientation) {
                    Vertical -> calculateOverScroll(available.y)
                    Horizontal -> calculateOverScroll(available.x)
                }
                return super.onPostScroll(consumed, available, source)
            }

            private fun calculateOverScroll(available: Float) {
                val previous = overScrollAmount.floatValue
                overScrollAmount.floatValue += available * (0.3f)
                overScrollAmount.floatValue = when {
                    previous > 0 -> overScrollAmount.floatValue.coerceAtLeast(0f)
                    previous < 0 -> overScrollAmount.floatValue.coerceAtMost(0f)
                    else -> overScrollAmount.floatValue
                }
            }
        }
    }
    return nestedScrollConnection
}

private sealed class FlipShape : Shape {
    data object Top : FlipShape() {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Rectangle(Rect(0f, 0f, size.width, size.height / 2))
    }

    data object Bottom : FlipShape() {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Rectangle(Rect(0f, size.height / 2, size.width, size.height))
    }

    data object Left : FlipShape() {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
    }

    data object Right : FlipShape() {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Rectangle(Rect(size.width / 2, 0f, size.width, size.height))
    }
}

@Preview
@Composable
private fun FlipPagerPreview() {
    ComposePlaygroundTheme {
        Surface {
            val state = rememberPagerState { 10 }

            FlipPager(
                modifier = Modifier
                    .padding(30.dp)
                    .aspectRatio(1.75f),
                state = state,
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Cyan),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "page: ${page + 1}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
