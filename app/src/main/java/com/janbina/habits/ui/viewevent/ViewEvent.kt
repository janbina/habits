package com.janbina.habits.ui.viewevent

abstract class ViewEvent {

    var isConsumed = false
        private set

    fun consume() {
        isConsumed = true
    }

}