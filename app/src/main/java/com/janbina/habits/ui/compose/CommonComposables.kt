package com.janbina.habits.ui.compose

import androidx.compose.foundation.Icon
import androidx.compose.foundation.contentColor
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ToolbarButton(
    asset: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = contentColor(),
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        androidx.compose.material.Icon(imageVector = asset, tint = tint)
    }
}