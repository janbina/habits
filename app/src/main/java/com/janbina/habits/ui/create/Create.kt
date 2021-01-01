package com.janbina.habits.ui.create

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.janbina.habits.ui.compose.HabitsTheme
import com.janbina.habits.ui.detail.HabitsAppBar
import com.janbina.habits.util.mavericksViewModelAndStateFragment

@Composable
fun CreateScreen(
    navController: NavController,
    viewModel: CreateViewModel,
) {

    val viewState by viewModel.liveData.observeAsState(initial = CreateState(null))

    val focusRequester = FocusRequester()

    HabitsTheme {
        Column {
            HabitsAppBar(
                onNavIconPressed = navController::navigateUp,
                actions = {
                    Button(onClick = viewModel::save) {
                        Text(text = "Save")
                    }
                }
            )
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester).fillMaxWidth()
                    .padding(16.dp),
                value = viewState.name,
                onValueChange = viewModel::nameChanged,
                label = {
                    Text(
                        text = "Name"
                    )
                },
                keyboardOptions = KeyboardOptions(),
            )
        }
    }
}

