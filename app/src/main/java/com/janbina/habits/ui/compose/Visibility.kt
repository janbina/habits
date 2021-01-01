package com.janbina.habits.ui.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

enum class Visibility {
    VISIBLE, INVISIBLE, GONE
}

fun Modifier.visible(visible: Boolean) = composed {
    this.then(VisibleModifier(if (visible) Visibility.VISIBLE else Visibility.GONE))
}

fun Modifier.invisible(invisible: Boolean) = composed {
    this.then(VisibleModifier(if (invisible) Visibility.INVISIBLE else Visibility.VISIBLE))
}

fun Modifier.gone(gone: Boolean) = composed {
    this.then(VisibleModifier(if (gone) Visibility.GONE else Visibility.VISIBLE))
}

private data class VisibleModifier(val visibility: Visibility) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        return if (visibility == Visibility.GONE) {
            layout(0, 0) {
                // Empty placement block
            }
        } else {
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                if (visibility == Visibility.VISIBLE) {
                    placeable.place(0, 0)
                }
            }
        }
    }
}
