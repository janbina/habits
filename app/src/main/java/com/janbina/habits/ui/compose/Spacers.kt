package com.janbina.habits.ui.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HorizontalSpacer(
    size: Dp,
    modifier: Modifier = Modifier,
) = Spacer(Modifier.preferredWidth(size).then(modifier))

@Composable
fun VerticalSpacer(
    size: Dp,
    modifier: Modifier = Modifier,
) = Spacer(Modifier.preferredHeight(size).then(modifier))