package com.mangbaam.composeplayground.pager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun <T> Hero(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoScrollDuration: Long = 3000,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    pageSize: PageSize = PageSize.Fill,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    userScrollEnabled: Boolean = true,
    onPageChanged: ((page: Int) -> Unit)? = null,
    content: @Composable PagerScope.(item: T) -> Unit,
) {
    val initialPage = if (items.size > 1) ((Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2) % items.size) else items.size
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { if (items.size > 1) Int.MAX_VALUE else items.size },
    )
    val dragging by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(dragging) {
        while (isActive && !dragging && items.size > 1) {
            delay(autoScrollDuration)
            if (pagerState.currentPage + 1 in 0..Int.MAX_VALUE) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } else {
                pagerState.scrollToPage(initialPage)
            }
        }
    }

    LaunchedEffect(onPageChanged != null) {
        if (onPageChanged != null) {
            snapshotFlow { pagerState.currentPage }
                .map { it % items.size }
                .collect(onPageChanged)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = contentPadding,
        pageSize = pageSize,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        userScrollEnabled = userScrollEnabled,
    ) { page ->
        val itemIndex = page % items.size
        items.getOrNull(itemIndex)?.let {
            content(it)
        }
    }
}

@Composable
private fun HeroIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    pageCount: Int,
) {
    Text(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                append(String.format(Locale.getDefault(), "%02d", currentPage))
            }
            append(String.format(Locale.getDefault(), " / %02d", pageCount))
        },
        style = MaterialTheme.typography.titleSmall,
        color = Color.DarkGray,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview
@Composable
private fun HeroPreview() {
    ComposePlaygroundTheme {
        val items = remember { List(2) { "Hero #${it + 1}" } }
        var currentPage by remember { mutableIntStateOf(1) }

        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(vertical = 100.dp),
        ) {
            Hero(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                items = items,
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 8.dp,
                onPageChanged = { page -> currentPage = page + 1 },
            ) { item ->
                Card(
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(item)

                    }
                }
            }
            HeroIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 28.dp),
                currentPage = currentPage,
                pageCount = items.size,
            )
        }
    }
}
