package com.mangbaam.composeplayground.extension

import androidx.compose.foundation.pager.PagerState

val PagerState.pageOffset: Float
    get() = currentPage + currentPageOffsetFraction

fun PagerState.offsetForPage(page: Int): Float = pageOffset - page
fun PagerState.startOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtLeast(0f)
fun PagerState.endOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtMost(1f)
