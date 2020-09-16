package com.janbina.habits.ui.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.janbina.habits.R
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.models.HabitDay
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.detail.HabitDetailFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class DayState(
    val day: Int,
    val habits: Async<List<HabitDay>>,
) : MavericksState {
    @Suppress("unused")
    constructor(args: DayFragment.Args) : this(
        args.day,
        Uninitialized
    )
}

class DayViewModel @AssistedInject constructor(
    @Assisted private val initialState: DayState,
    private val habitsRepository: HabitsRepository,
) : BaseViewModel<DayState>(initialState) {

    init {
        habitsRepository.getHabitsForDay(initialState.day).onEach { habits ->
            habits.success {
                setState { copy(habits = Success(it)) }
            }
            habits.failure {
                setState { copy(habits = Fail(it)) }
            }
        }.launchIn(viewModelScope)
    }

    fun markHabitAsCompleted(habit: HabitDay, completed: Boolean) {
        habitsRepository.setHabitComplete(habit.id, initialState.day, completed)
    }

    fun openHabit(habit: HabitDay) {
        NavigationEvent(R.id.habitDetailFragment, HabitDetailFragment.Args(
            habit.id, initialState.day
        )).publish()
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<DayViewModel, DayState> {
        override fun create(initialState: DayState): DayViewModel
    }

    companion object :
        DaggerVmFactory<DayViewModel, DayState>(DayViewModel::class.java)
}