package com.mangbaam.composeplayground.stepper

import androidx.compose.ui.graphics.Color

object StepperDefaults {
    fun colors(
        enabled: Color = Color(0xFFD8DBE5),
        disabled: Color = Color(0xFF434753),
        container: Color = Color(0xFF2E303A),
        content: Color = Color.White,
    ): StepperColors = StepperColors(enabled = enabled, disabled = disabled, container = container, content = content)
}

data class StepperColors(
    val enabled: Color,
    val disabled: Color,
    val container: Color,
    val content: Color,
)
