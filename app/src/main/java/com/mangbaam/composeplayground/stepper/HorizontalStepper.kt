package com.mangbaam.composeplayground.stepper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> HorizontalStepper(
    currentItem: T,
    modifier: Modifier = Modifier,
    leftEnabled: Boolean = true,
    rightEnabled: Boolean = true,
    colors: StepperColors = StepperDefaults.colors(),
    leftIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "stepper left icon"
        )
    },
    rightIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "stepper right icon"
        )
    },
    onClickLeft: (current: T) -> Unit,
    onClickRight: (current: T) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = leftEnabled,
                        role = Role.Button,
                        onClick = { onClickLeft(currentItem) },
                    )
            ) {
                CompositionLocalProvider(LocalContentColor provides if (leftEnabled) colors.enabled else colors.disabled) {
                    leftIcon()
                }
            }
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 32.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = currentItem.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.content,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = rightEnabled,
                        role = Role.Button,
                        onClick = { onClickRight(currentItem) },
                    )
            ) {
                CompositionLocalProvider(LocalContentColor provides if (rightEnabled) colors.enabled else colors.disabled) {
                    rightIcon()
                }
            }
        }
    }
}
