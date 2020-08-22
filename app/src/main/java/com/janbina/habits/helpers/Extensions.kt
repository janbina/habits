package com.janbina.habits.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.view.View
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

fun View.show() {
    if (visibility != View.VISIBLE) {
        scaleX = 0F
        scaleY = 0F
        animate().scaleX(1F).scaleY(1F).setDuration(110).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = View.VISIBLE
            }
        })
    }
}

fun View.hide() {
    if (visibility == View.VISIBLE) {
        animate().scaleX(0F).scaleY(0F).setDuration(110).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.INVISIBLE
            }
        })
    }
}

fun View.showOrHide(showCondition: Boolean) {
    if (showCondition) show() else hide()
}