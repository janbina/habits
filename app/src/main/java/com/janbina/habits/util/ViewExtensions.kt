package com.janbina.habits.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment

fun EditText.updateText(new: String) {
    if (text?.toString() != new) {
        setText(new)
        setSelection(new.length)
    }
}

fun View.hideIme() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showIme() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, 0)
}

fun View.showImeDelayed() {
    Handler().postDelayed(100L) { showIme() }
}

fun Fragment.hideIme() {
    activity?.currentFocus?.hideIme()
}

private val Context.inputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager