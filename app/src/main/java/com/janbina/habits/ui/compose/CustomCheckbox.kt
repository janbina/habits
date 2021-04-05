package com.janbina.habits.ui.compose

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp

private val CheckDrawFraction = FloatPropKey()
private const val BoxInDuration = 50
private const val BoxOutDuration = 100
private const val CheckAnimationDuration = 100
private val TransitionDefinition = transitionDefinition<Boolean> {
    state(true) {
        this[CheckDrawFraction] = 1f
    }
    state(false) {
        this[CheckDrawFraction] = 0f
    }
    transition(false to true) {
        CheckDrawFraction using tween(
            durationMillis = CheckAnimationDuration
        )
    }
    transition(true to false) {
        CheckDrawFraction using keyframes {
            durationMillis = BoxOutDuration
            1f at 0
            1f at BoxOutDuration - 1
            0f at BoxOutDuration
        }
    }
}

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
    interactionState: InteractionState = remember { InteractionState() },
//    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    val state = transition(definition = TransitionDefinition, toState = checked)

    Box(
        modifier = Modifier.size(30.dp).draggable()
            .border(2.dp, Color.Black, CircleShape)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = true,
                interactionState = interactionState,
                indication = rememberRipple(
                    bounded = false,
                    radius = 24.dp,
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size((state[CheckDrawFraction] * 30).dp)
                .background(Color.Blue, CircleShape)
        )
    }
}