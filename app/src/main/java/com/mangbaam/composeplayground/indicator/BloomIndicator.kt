package com.mangbaam.composeplayground.indicator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.extension.offsetForPage
import com.mangbaam.composeplayground.pager.CenterAlignedHorizontalPager
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BloomIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    count: Int = pagerState.pageCount,
    dotColor: Color = Color.Black,
    unFocusedAlpha: Float = 0.3f,
    dotClickable: Boolean = true,
    onClickDot: (index: Int) -> Unit = {},
) {
    Row(
        modifier = modifier.padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) {
            val pageOffset = pagerState.offsetForPage(it).toDouble()
            val scale = (1.0 - pageOffset.absoluteValue.coerceAtMost(1.0)).absoluteValue
            val offset = scale * 2.3
            val alpha = scale.toFloat().coerceAtLeast(unFocusedAlpha.coerceIn(0f..1f) / 4)

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = dotClickable,
                        role = Role.Tab,
                        onClick = { onClickDot(it) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .offset(x = -offset.dp, y = -offset.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = alpha)),
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .offset(x = offset.dp, y = -offset.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = alpha)),
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .offset(x = -offset.dp, y = offset.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = alpha)),
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .offset(x = offset.dp, y = offset.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = alpha)),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun BloomIndicatorPreview() {
    ComposePlaygroundTheme {
        Surface {
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { 5 })

            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                BloomIndicator(
                    modifier = Modifier.padding(top = 32.dp),
                    pagerState = pagerState,
                    onClickDot = {
                        scope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    },
                )
            }
        }
    }
}
