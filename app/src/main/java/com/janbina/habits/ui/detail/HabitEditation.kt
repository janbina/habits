package com.janbina.habits.ui.detail

import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.janbina.habits.ui.compose.HabitsTextField
import com.janbina.habits.ui.compose.HorizontalSpacer

data class HabitEditationState(
    val id: String? = null,
    val name: String = "",
) {
    fun isValid() = name.isNotBlank()
}

@Composable
fun HabitEditation(
    modifier: Modifier = Modifier,
    state: HabitEditationState,
    onStateChange: (HabitEditationState) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    val interactionState = remember {
        InteractionState().apply { addInteraction(Interaction.Focused) }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HabitsTextField(
            modifier = Modifier.weight(1F),
            value = state.name,
            onValueChange = {
                onStateChange(state.copy(name = it))
            },
            interactionState = interactionState,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            onImeActionPerformed = { onSave() },
            singleLine = true,
        )
        HorizontalSpacer(size = 4.dp)
        IconButton(onClick = { onCancel() }) {
            Icon(imageVector = Icons.Default.Close)
        }
        IconButton(enabled = state.isValid(), onClick = { onSave() }) {
            Icon(imageVector = Icons.Default.Check)
        }
    }
}