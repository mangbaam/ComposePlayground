package com.mangbaam.composeplayground.extension

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState

@OptIn(ExperimentalFoundationApi::class)
val PagerState.pageOffset: Float
    get() = currentPage + currentPageOffsetFraction

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float =
    pageOffset - page
