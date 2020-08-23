package com.janbina.habits.ui.detail

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.kizitonwose.calendarview.utils.yearMonth
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

data class HabitDetailState(
    val id: String,
    val selectedMonth: YearMonth,
    val firstDayOfWeek: DayOfWeek,
    val habitDetail: Async<HabitsRepository.HabitDetail>
) : MvRxState {
    @Suppress("unused")
    constructor(args: HabitDetailFragment.Args) : this(
        args.id,
        LocalDate.ofEpochDay(args.day.toLong()).yearMonth,
        WeekFields.of(Locale.getDefault()).firstDayOfWeek,
        Uninitialized
    )
}

class HabitDetailViewModel @AssistedInject constructor(
    @Assisted private val initialState: HabitDetailState,
    private val habitsRepository: HabitsRepository,
) : BaseViewModel<HabitDetailState>(initialState) {

    init {
        viewModelScope.launch {
            habitsRepository.getHabitDetail(initialState.id).collect { detail ->
                detail.success {
                    setState { copy(habitDetail = Success(it)) }
                }
                detail.failure {
                    setState { copy(habitDetail = Fail(it)) }
                }
            }
        }
    }

    fun monthSelected(yearMonth: YearMonth) {
        setState { copy(selectedMonth = yearMonth) }
    }

    fun delete() {
        habitsRepository.deleteHabit(initialState.id)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HabitDetailViewModel, HabitDetailState> {
        override fun create(initialState: HabitDetailState): HabitDetailViewModel
    }

    companion object :
        DaggerVmFactory<HabitDetailViewModel, HabitDetailState>(HabitDetailViewModel::class.java)

}