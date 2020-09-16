package com.janbina.habits.di.helpers

import com.airbnb.mvrx.MavericksState
import com.janbina.habits.ui.base.BaseViewModel

interface AssistedViewModelFactory<VM : BaseViewModel<S>, S : MavericksState> {
    fun create(initialState: S): VM
}