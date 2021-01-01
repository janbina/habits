package com.janbina.habits.ui.home

import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.janbina.habits.R
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.models.Async
import com.janbina.habits.models.HabitDay
import com.janbina.habits.models.Uninitialized
import com.janbina.habits.models.toAsync
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class DayState(
    val day: Int,
    val habits: Async<List<HabitDay>>,
)

class DayViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository,
) : BaseReduxVM<DayState>(DayState(22, Uninitialized)) {

    init {
        habitsRepository.getHabitsForDay(currentState().day).onEach {
            setState { copy(habits = it.toAsync(habits())) }
        }.launchIn(viewModelScope)
    }

    fun markHabitAsCompleted(habit: HabitDay, completed: Boolean) {
        habitsRepository.setHabitComplete(habit.id, currentState().day, completed)
    }

    fun openHabit(habit: HabitDay) {
        NavigationEvent(R.id.habitDetailFragment, bundleOf()).publish()
    }
}