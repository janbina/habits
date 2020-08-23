package com.janbina.habits.ui.detail

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
import com.janbina.habits.ui.create.CreateFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.kizitonwose.calendarview.utils.yearMonth
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        habitsRepository.getHabitDetail(initialState.id).onEach { detail ->
            detail.success {
                setState { copy(habitDetail = Success(it)) }
            }
            detail.failure {
                setState { copy(habitDetail = Fail(it)) }
            }
        }.launchIn(viewModelScope)
    }

    fun monthSelected(yearMonth: YearMonth) {
        setState { copy(selectedMonth = yearMonth) }
    }

    fun toggleHabitCompletion(day: LocalDate) = withState {
        val habit = it.habitDetail() ?: return@withState
        val epochDay = day.toEpochDay().toInt()
        habitsRepository.setHabitComplete(it.id, epochDay, habit.days.contains(epochDay.toLong()).not())
    }

    fun delete() {
        habitsRepository.deleteHabit(initialState.id)
        NavigationEvent.back().publish()
    }

    fun edit() {
        NavigationEvent(
            R.id.createFragment,
            CreateFragment.Args(initialState.id).toBundle()
        ).publish()
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HabitDetailViewModel, HabitDetailState> {
        override fun create(initialState: HabitDetailState): HabitDetailViewModel
    }

    companion object :
        DaggerVmFactory<HabitDetailViewModel, HabitDetailState>(HabitDetailViewModel::class.java)

}