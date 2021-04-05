package com.janbina.habits.ui.compose

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

/**
 * TextField with custom design and two extra features:
 *  - tries to show keyboard after creation (if [autoShowKeyboard] true)
 *  - you can specify initial cursor position (also possible with BasicTextField, but you have to
 *    deal with [TextFieldValue] yourself...)
 */
@Composable
fun HabitsTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    autoShowKeyboard: Boolean = true,
    cursorPosition: Int = value.length,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    interactionState: InteractionState = remember { InteractionState() },
    cursorColor: Color = Color.Black
) {
    val keyboardController: Ref<SoftwareKeyboardController> = remember { Ref() }

    val focusRequester = FocusRequester()
    val textFieldModifier = modifier
        .focusRequester(focusRequester)
        .let {
            it.clickable(interactionState = interactionState, indication = null) {
                focusRequester.requestFocus()
                keyboardController.value?.showSoftwareKeyboard()
            }
        }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(cursorPosition)
            )
        )
    }
    val textFieldValue = textFieldValueState.copy(text = value)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .then(textFieldModifier)
        ) {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = textFieldValue,
                onValueChange = {
                    textFieldValueState = it
                    if (value != it.text) {
                        onValueChange(it.text)
                    }
                },
                interactionState = interactionState,
                keyboardOptions = keyboardOptions,
                onImeActionPerformed = onImeActionPerformed,
                singleLine = singleLine,
                maxLines = maxLines,
                textStyle = textStyle,
                visualTransformation = visualTransformation,
                cursorColor = cursorColor,
                onTextInputStarted = {
                    keyboardController.value = it
                    onTextInputStarted(it)
                },
                onTextLayout = onTextLayout,
            )
        }
    }

    if (autoShowKeyboard) {
        val lifecycleOwner = AmbientLifecycleOwner.current

        onActive(callback = {
            lifecycleOwner.lifecycleScope.launchWhenStarted {
                focusRequester.requestFocus()
                delay(300)
                keyboardController.value?.showSoftwareKeyboard()
            }
        })
    }
}
