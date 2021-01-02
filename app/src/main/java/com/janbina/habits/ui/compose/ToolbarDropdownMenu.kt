package com.janbina.habits.ui.compose

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp

typealias ToolbarAction = () -> Unit

@Composable
fun ToolbarDropdownMenu(
    imageVector: ImageVector,
    items: List<Pair<String, ToolbarAction?>>,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    DropdownMenu(
        toggle = {
            ToolbarButton(
                asset = imageVector,
                onClick = { menuExpanded = true })
        },
        expanded = menuExpanded,
        dropdownOffset = Position(0.dp, (-54).dp),
        onDismissRequest = { menuExpanded = false }
    ) {
        items.forEach {
            DropdownMenuItem(
                onClick = {
                    it.second?.invoke()
                    menuExpanded = false
                }
            ) {
                Text(text = it.first)
            }
        }
    }
}