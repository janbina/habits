package com.janbina.habits.ui.home

import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.janbina.habits.R
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.create.CreateFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now(),
    val daysShown: List<LocalDate> = (-4L..2L).map { selectedDate.plusDays(it) }
)

class HomeViewModel @ViewModelInject constructor(

) : BaseReduxVM<HomeState>(HomeState()) {

    fun dateChanged(newDate: LocalDate) = viewModelScope.launchSetState {
        copy(selectedDate = newDate)
    }

    fun goToSettings() {
        NavigationEvent(R.id.settingsFragment).publish()
    }

    fun goToHabitCreation() {
        NavigationEvent(R.id.createFragment, bundleOf()).publish()
    }
}