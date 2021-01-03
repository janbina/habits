package com.janbina.habits.ui.compose

import androidx.compose.foundation.layout.ColumnScope
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
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    items: List<Pair<String, ToolbarAction?>>,
) {
    ToolbarDropdownMenu(
        imageVector = imageVector,
        expanded = expanded,
        setExpanded = setExpanded
    ) {
        items.forEach {
            SimpleDropdownMenuItem(
                text = it.first,
                action = it.second,
                setExpanded = setExpanded
            )
        }
    }
}

@Composable
fun ToolbarDropdownMenu(
    imageVector: ImageVector,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        toggle = {
            ToolbarButton(
                asset = imageVector,
                onClick = { setExpanded(true) })
        },
        expanded = expanded,
        dropdownOffset = Position(0.dp, (-54).dp),
        onDismissRequest = { setExpanded(false) },
        dropdownContent = content,
    )
}

@Composable
fun SimpleDropdownMenuItem(
    text: String,
    action: ToolbarAction?,
    setExpanded: (Boolean) -> Unit,
) {
    DropdownMenuItem(
        onClick = {
            action?.invoke()
            setExpanded(false)
        }
    ) {
        Text(text = text)
    }
}