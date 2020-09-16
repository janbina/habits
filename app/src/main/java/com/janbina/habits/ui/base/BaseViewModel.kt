package com.janbina.habits.ui.base

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.janbina.habits.ui.viewevent.ViewEvent
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<S : MavericksState>(initialState: S) : MavericksViewModel<S>(initialState) {

    private val _viewEvents = MutableStateFlow<ViewEvent?>(null)
    val viewEvents: StateFlow<ViewEvent?> get() = _viewEvents

    fun ViewEvent.publish() {
        _viewEvents.value = this
    }

    inline fun <reified T : ViewEvent> getEventFlow(): Flow<T> {
        return viewEvents
            .filterNotNull()
            .filterIsInstance()
    }

    inline fun <reified T : ViewEvent> onEachEvent(crossinline action: (T) -> Unit): Flow<T> {
        return getEventFlow<T>()
            .filter { it.isConsumed.not() }
            .onEach {
                it.consume()
                action(it)
            }
    }
}