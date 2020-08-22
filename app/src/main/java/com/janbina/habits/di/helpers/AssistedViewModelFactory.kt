package com.janbina.habits.di.helpers

import com.airbnb.mvrx.MvRxState
import com.janbina.habits.ui.base.BaseViewModel

interface AssistedViewModelFactory<VM : BaseViewModel<S>, S : MvRxState> {
    fun create(state: S): VM
}