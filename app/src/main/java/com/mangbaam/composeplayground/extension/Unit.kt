package com.mangbaam.composeplayground.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

val Dp.px: Float
    @Composable
    get() = with(LocalDensity.current) { this@px.toPx() }

val TextUnit.px: Float
    @Composable
    get() = with(LocalDensity.current) { this@px.toPx() }

val Float.toDp: Dp
@Composable
get() = with(LocalDensity.current) { this@toDp.toDp() }
