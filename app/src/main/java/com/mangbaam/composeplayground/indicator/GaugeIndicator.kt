package com.mangbaam.composeplayground.indicator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.extension.pageOffset
import com.mangbaam.composeplayground.extension.toDp
import com.mangbaam.composeplayground.pager.CenterAlignedHorizontalPager
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GaugeIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFFE2D3C3),
    progressColor: Color = MaterialTheme.colorScheme.primary,
    divider: @Composable ((Int) -> Unit)? = null,
    onPress: (Float) -> Unit = {},
    onDrag: (Float) -> Unit = {},
) {
    var width by remember { mutableFloatStateOf(1f) }
    var height by remember { mutableFloatStateOf(1f) }
    Box(
        modifier = modifier
            .background(trackColor)
            .defaultMinSize(minWidth = 30.dp, minHeight = 5.dp)
            .size(width = 60.dp, height = 5.dp)
            .graphicsLayer {
                width = size.width
                height = size.height
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress(it.x / width.coerceAtLeast(1.0f))
                    },
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    onDrag(change.position.x / width.coerceAtLeast(1.0f))
                }
            },
    ) {
        val weight =
            ((pagerState.currentPage + pagerState.currentPageOffsetFraction).absoluteValue + 1) / pagerState.pageCount
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(weight)
                .background(progressColor),
        )
        if (divider != null) {
            Row(
                modifier = Modifier.size(width = width.toDp, height = height.toDp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(pagerState.pageCount - 1) {
                    divider(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun GaugeIndicatorPreview() {
    ComposePlaygroundTheme {
        Surface {
            val pagerState = rememberPagerState(pageCount = { 5 })
            var touched by remember { mutableFloatStateOf(0f) }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = touched.toString())
                CenterAlignedHorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fixed(300.dp),
                    pageSpacing = 12.dp,
                ) { page ->
                    Card {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "Page #${page + 1}")
                        }
                    }
                }
                GaugeIndicator(
                    modifier = Modifier
                        .padding(20.dp)
                        .size(width = 150.dp, height = 30.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    pagerState = pagerState,
                    onPress = {
                        touched = it
                        scope.launch {
                            pagerState.animateScrollToPage(page = (pagerState.pageCount * it).toInt())
                        }
                    },
                    onDrag = {
                        touched = it
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = (pagerState.pageCount * it).toInt(),
                                pageOffsetFraction = (it - pagerState.pageOffset).coerceIn(
                                    minimumValue = -0.5f,
                                    maximumValue = 0.5f,
                                )
                            )
                        }
                    },
                    divider = { VerticalDivider(color = Color.White) },
                )
            }
        }
    }
}
