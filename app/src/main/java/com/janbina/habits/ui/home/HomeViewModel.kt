package com.janbina.habits.ui.home

import com.airbnb.mvrx.MvRxState
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now()
) : MvRxState

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState,
) : BaseViewModel<HomeState>(initialState) {

    fun dateChanged(newDate: LocalDate) = setState {
        copy(selectedDate = newDate)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(initialState: HomeState): HomeViewModel
    }

    companion object :
        DaggerVmFactory<HomeViewModel, HomeState>(HomeViewModel::class.java)

}