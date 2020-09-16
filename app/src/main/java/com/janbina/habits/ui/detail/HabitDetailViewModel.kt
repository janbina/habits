package com.janbina.habits.ui.detail

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.janbina.habits.R
import com.janbina.habits.data.repository.DaysRepository
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.create.CreateFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.utils.yearMonth
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class HabitDetailState(
    val args: HabitDetailFragment.Args,
    val selectedMonth: YearMonth,
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val habitDetail: Async<HabitsRepository.HabitDetail>,
    val days: List<DayOfWeek>
) : MavericksState {
    @Suppress("unused")
    constructor(args: HabitDetailFragment.Args) : this(
        args,
        LocalDate.ofEpochDay(args.day.toLong()).yearMonth,
        YearMonth.now().minusYears(1),
        YearMonth.now().plusYears(1),
        Uninitialized,
        DayOfWeek.values().toList()
    )
}

class HabitDetailViewModel @AssistedInject constructor(
    @Assisted private val initialState: HabitDetailState,
    private val habitsRepository: HabitsRepository,
    private val daysRepository: DaysRepository
) : BaseViewModel<HabitDetailState>(initialState) {

    private val args = initialState.args
    private val day = LocalDate.ofEpochDay(args.day.toLong())

    init {
        setState {
            copy(days = daysRepository.getDaysOfWeekSorted())
        }

        habitsRepository.getHabitDetail(initialState.args.id).onEach { detail ->
            detail.success {
                setState {
                    copy(
                        habitDetail = Success(it),
                        startMonth = getStart(it),
                        endMonth = getEnd(it),
                    )
                }
            }
            detail.failure {
                setState { copy(habitDetail = Fail(it)) }
            }
        }.launchIn(viewModelScope)
    }

    fun monthSelected(calendarMonth: CalendarMonth) = withState {
        setState { copy(selectedMonth = calendarMonth.yearMonth) }
    }

    fun toggleHabitCompletion(day: LocalDate) = withState {
        val habit = it.habitDetail() ?: return@withState
        val epochDay = day.toEpochDay().toInt()
        habitsRepository.setHabitComplete(
            it.args.id,
            epochDay,
            habit.days.contains(epochDay.toLong()).not()
        )
    }

    fun delete() {
        habitsRepository.deleteHabit(initialState.args.id)
        NavigationEvent.back().publish()
    }

    fun edit() {
        NavigationEvent(
            R.id.createFragment,
            CreateFragment.Args(initialState.args.id).toBundle()
        ).publish()
    }

    private fun getStart(detail: HabitsRepository.HabitDetail): YearMonth {
        var start = detail.days.firstOrNull()?.let { LocalDate.ofEpochDay(it) }
        if (start == null || start.isAfter(day)) {
            start = day
        }
        return start!!.yearMonth.minusYears(1)
    }

    private fun getEnd(detail: HabitsRepository.HabitDetail): YearMonth {
        var end = detail.days.lastOrNull()?.let { LocalDate.ofEpochDay(it) }
        if (end == null || end.isBefore(day)) {
            end = day
        }
        return end!!.yearMonth.plusYears(1)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HabitDetailViewModel, HabitDetailState> {
        override fun create(initialState: HabitDetailState): HabitDetailViewModel
    }

    companion object :
        DaggerVmFactory<HabitDetailViewModel, HabitDetailState>(HabitDetailViewModel::class.java)

}