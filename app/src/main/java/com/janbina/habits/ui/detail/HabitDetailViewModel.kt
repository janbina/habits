package com.janbina.habits.ui.detail

import com.airbnb.mvrx.*
import com.github.kittinunf.result.Result
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class HabitDetailState(
    val id: String,
    val day: LocalDate,
    val selectedMonth: YearMonth = YearMonth.now(),
    val startMonth: YearMonth = YearMonth.now().minusYears(1),
    val endMonth: YearMonth = YearMonth.now().plusYears(1),
    val habitDetail: Async<HabitsRepository.HabitDetail> = Uninitialized,
    val days: List<DayOfWeek> = DayOfWeek.values().toList()
) : MavericksState {

    @Suppress("unused")
    constructor(args: HabitDetailFragment.Args): this(
        id = args.id,
        day = LocalDate.ofEpochDay(args.day.toLong())
    )
}

class HabitDetailViewModel @AssistedInject constructor(
    @Assisted initialState: HabitDetailState,
    private val habitsRepository: HabitsRepository,
    private val daysRepository: DaysRepository
) : BaseViewModel<HabitDetailState>(initialState) {

    private val id = initialState.id
    private val day = initialState.day

    init {
        setState {
            copy(
                selectedMonth = day.yearMonth,
                days = daysRepository.getDaysOfWeekSorted()
            )
        }

        habitsRepository.getHabitDetail(id).setOnEach {
            when (it) {
                is Result.Success -> {
                    copy(
                        habitDetail = Success(it.value),
                        startMonth = getStart(it.value),
                        endMonth = getEnd(it.value),
                    )
                }
                is Result.Failure -> {
                    copy(habitDetail = Fail(it.error))
                }
            }
        }
    }

    fun monthSelected(calendarMonth: CalendarMonth) = setState {
        copy(selectedMonth = calendarMonth.yearMonth)
    }

    fun toggleHabitCompletion(day: LocalDate) = withState {
        val habit = it.habitDetail() ?: return@withState
        val epochDay = day.toEpochDay().toInt()
        habitsRepository.setHabitComplete(
            habit.habit.id,
            epochDay,
            habit.days.contains(epochDay.toLong()).not()
        )
    }

    fun delete() {
        habitsRepository.deleteHabit(id)
        NavigationEvent.back().publish()
    }

    fun edit() {
        NavigationEvent(
            R.id.createFragment,
            CreateFragment.Args(id).toBundle()
        ).publish()
    }

    private fun getStart(detail: HabitsRepository.HabitDetail): YearMonth {
        var start = detail.days.firstOrNull()?.let { LocalDate.ofEpochDay(it) }
        if (start == null || start.isAfter(day)) {
            start = day
        }
        return start.yearMonth.minusYears(1)
    }

    private fun getEnd(detail: HabitsRepository.HabitDetail): YearMonth {
        var end = detail.days.lastOrNull()?.let { LocalDate.ofEpochDay(it) }
        if (end == null || end.isBefore(day)) {
            end = day
        }
        return end.yearMonth.plusYears(1)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<HabitDetailViewModel, HabitDetailState> {
        override fun create(initialState: HabitDetailState): HabitDetailViewModel
    }

    companion object :
        DaggerVmFactory<HabitDetailViewModel, HabitDetailState>(HabitDetailViewModel::class.java)
}