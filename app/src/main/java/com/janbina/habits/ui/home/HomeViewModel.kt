package com.janbina.habits.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.janbina.habits.R
import com.janbina.habits.data.preferences.Datastore
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.detail.HabitEditationState
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.now(),
    val daysShown: List<LocalDate> = (-4L..2L).map { selectedDate.plusDays(it) },
    val habitEditationState: HabitEditationState = HabitEditationState(),
    val habitEditationVisible: Boolean = false,
    val showArchived: Boolean = false,
)

class HomeViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository,
    private val dataStore: Datastore,
) : BaseReduxVM<HomeState>(HomeState()) {

    init {
        dataStore.isShowArchived.onEach {
            setState { copy(showArchived = it) }
        }.launchIn(viewModelScope)
    }

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

    fun setShowArchived(value: Boolean) = viewModelScope.launch {
        dataStore.setShowArchived(value)
    }
}