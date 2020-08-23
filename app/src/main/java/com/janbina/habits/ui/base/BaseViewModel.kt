package com.janbina.habits.ui.base

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState
import com.janbina.habits.ui.viewevent.ViewEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<S : MvRxState>(initialState: S) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    private val _viewEvents = MutableStateFlow<ViewEvent?>(null)
    val viewEvents: StateFlow<ViewEvent?> get() = _viewEvents

    fun ViewEvent.publish() {
        _viewEvents.value = this
    }
}