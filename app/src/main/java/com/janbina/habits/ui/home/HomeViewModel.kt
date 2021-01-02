package com.janbina.habits.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.janbina.habits.R
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.detail.HabitEditationState
import com.janbina.habits.ui.viewevent.NavigationEvent
import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now(),
    val daysShown: List<LocalDate> = (-4L..2L).map { selectedDate.plusDays(it) },
    val habitEditationState: HabitEditationState = HabitEditationState(),
    val habitEditationVisible: Boolean = false,
)

class HomeViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) : BaseReduxVM<HomeState>(HomeState()) {

    fun dateChanged(newDate: LocalDate) = viewModelScope.launchSetState {
        copy(selectedDate = newDate)
    }

    fun goToSettings() {
        NavigationEvent(R.id.settingsFragment).publish()
    }

    fun createHabit() {
        viewModelScope.launchSetState {
            copy(habitEditationState = HabitEditationState(), habitEditationVisible = true)
        }
    }

    fun updateCreation(editationState: HabitEditationState) = viewModelScope.launchSetState {
        copy(habitEditationState = editationState)
    }

    fun cancelCreation() = viewModelScope.launchSetState {
        copy(habitEditationVisible = false)
    }

    fun saveCreation() = viewModelScope.launchSetState {
        if (!habitEditationState.isValid()) {
            copy(habitEditationVisible = false)
        } else {
            habitsRepository.saveHabit(habitEditationState.id, habitEditationState.name)
            copy(habitEditationVisible = false)
        }
    }
}