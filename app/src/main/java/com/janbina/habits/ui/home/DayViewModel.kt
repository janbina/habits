package com.janbina.habits.ui.home

import androidx.lifecycle.viewModelScope
import com.janbina.habits.R
import com.janbina.habits.data.preferences.Datastore
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.models.*
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.detail.HabitDetailFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

data class DayState(
    val day: Long,
    val habits: Async<Day> = Uninitialized,
    val showArchived: Boolean = false,
)

class DayViewModel @AssistedInject constructor(
    @Assisted initialState: DayState,
    private val habitsRepository: HabitsRepository,
    private val dataStore: Datastore,
) : BaseReduxVM<DayState>(initialState) {

    init {
        habitsRepository.getHabitsForDay(currentState().day).onEach {
            setState { copy(habits = it.toAsync(habits())) }
        }.launchIn(viewModelScope)

        dataStore.isShowArchived.onEach {
            setState { copy(showArchived = it) }
        }.launchIn(viewModelScope)
    }

    fun markHabitAsCompleted(habit: HabitOnDay, completed: Boolean) {
        habitsRepository.setHabitComplete(habit.id, currentState().day, completed)
    }

    fun openHabit(habit: HabitOnDay) {
        NavigationEvent(R.id.habitDetailFragment, HabitDetailFragment.createArgs(habit.id, LocalDate.ofEpochDay(currentState().day.toLong()))).publish()
    }

    @AssistedInject.Factory
    internal interface Factory {
        fun create(initialState: DayState): DayViewModel
    }
}

internal fun DayViewModel.Factory.create(
    day: Long,
): DayViewModel {
    return create(DayState(day = day))
}