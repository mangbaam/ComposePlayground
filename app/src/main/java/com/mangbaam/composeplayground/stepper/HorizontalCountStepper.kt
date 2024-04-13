package com.mangbaam.composeplayground.stepper

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme

@Composable
fun HorizontalCountStepper(
    modifier: Modifier = Modifier,
    minCount: Int = 1,
    maxCount: Int = Int.MAX_VALUE,
    currentCount: Int = 1,
    onClickMinus: (current: Int) -> Unit = {},
    onClickPlus: (current: Int) -> Unit = {},
) {
    HorizontalStepper(
        modifier = modifier,
        currentItem = currentCount,
        leftEnabled = currentCount != minCount,
        rightEnabled = currentCount != maxCount,
        onClickLeft = onClickMinus,
        onClickRight = onClickPlus,
    )
}

@Preview
@Composable
private fun HorizontalCountStepperPreview() {
    var current by remember { mutableIntStateOf(1) }
    ComposePlaygroundTheme {
        HorizontalCountStepper(
            modifier = Modifier.size(width = 100.dp, height = 30.dp),
            currentCount = current,
            maxCount = 10,
            onClickPlus = { current++ },
            onClickMinus = { current-- }
        )
    }
}
