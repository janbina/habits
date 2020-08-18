package com.janbina.habits

import android.content.res.Resources
import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.argOr(key: String, default: T): T {
    val arg = arguments?.get(key)
    if (arg is T) {
        return arg
    }
    return default
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()