package com.janbina.habits.ui.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.models.HabitDay
import com.janbina.habits.ui.base.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class DayState(
    val day: Int,
    val habits: Async<List<HabitDay>>,
) : MvRxState {
    @Suppress("unused")
    constructor(args: DayFragment.Args) : this(
        args.day,
        Uninitialized
    )
}

class DayViewModel @AssistedInject constructor(
    @Assisted state: DayState,
    private val habitsRepository: HabitsRepository,
) : BaseViewModel<DayState>(state) {

    init {
        withState {
            viewModelScope.launch {
                habitsRepository.getHabitsForDay(it.day).collect { habits ->
                    habits.success {

                        setState { copy(habits = Success(it)) }
                    }
                    habits.failure {
                        setState { copy(habits = Fail(it)) }
                    }
                }
            }
        }
    }

    fun markHabitAsCompleted(habit: HabitDay, completed: Boolean) {
        withState {
            habitsRepository.setHabitComplete(habit.id, it.day, completed)
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<DayViewModel, DayState> {
        override fun create(state: DayState): DayViewModel
    }

    companion object :
        DaggerVmFactory<DayViewModel, DayState>(DayViewModel::class.java)
}