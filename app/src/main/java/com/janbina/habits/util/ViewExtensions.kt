package com.janbina.habits.util

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kizitonwose.calendarview.CalendarView

fun EditText.updateText(new: String) {
    if (text?.toString() != new) {
        setText(new)
        setSelection(new.length)
    }
}

inline fun ViewPager2.onPageSelected(crossinline action: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            action(position)
        }
    })
}

fun CalendarView.disableScroll() {
    addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING
        }
    })
}

fun Toolbar.setMenuActions(actions: Map<Int, () -> Unit>) {
    setOnMenuItemClickListener { item ->
        actions[item.itemId]?.invoke() != null
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