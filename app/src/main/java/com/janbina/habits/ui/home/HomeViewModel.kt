package com.janbina.habits.ui.home

import com.airbnb.mvrx.MvRxState
import com.janbina.habits.R
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.create.CreateFragment
import com.janbina.habits.ui.create.CreateFragmentCompose
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now(),
    val daysShown: List<LocalDate> = (-4L..2L).map { selectedDate.plusDays(it) }
) : MvRxState

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState,
) : BaseViewModel<HomeState>(initialState) {

    fun dateChanged(newDate: LocalDate) = setState {
        copy(selectedDate = newDate)
    }

    fun goToSettings() {
        NavigationEvent(R.id.settingsFragment).publish()
    }

    fun goToHabitCreation() {
        NavigationEvent(R.id.createFragmentCompose, CreateFragmentCompose.Args()).publish()
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(initialState: HomeState): HomeViewModel
    }

    companion object :
        DaggerVmFactory<HomeViewModel, HomeState>(HomeViewModel::class.java)

}