package com.janbina.habits.util

import android.widget.EditText

fun EditText.updateText(new: String) {
    if (text?.toString() != new) {
        setText(new)
        setSelection(new.length)
    }
}