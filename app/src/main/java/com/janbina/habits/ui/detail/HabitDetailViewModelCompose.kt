package com.janbina.habits.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.github.kittinunf.result.Result
import com.janbina.habits.R
import com.janbina.habits.data.repository.DaysRepository
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseComposeViewModel
import com.janbina.habits.ui.base.getArgs
import com.janbina.habits.ui.create.CreateFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class HabitDetailStateCompose(
    val args: HabitDetailFragmentCompose.Args,
    val selectedMonth: YearMonth,
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val habitDetail: Async<HabitsRepository.HabitDetail>,
    val days: List<DayOfWeek>
) {
    constructor(args: HabitDetailFragmentCompose.Args) : this(
        args,
        LocalDate.ofEpochDay(args.day.toLong()).yearMonth,
        YearMonth.now().minusYears(1),
        YearMonth.now().plusYears(1),
        Uninitialized,
        DayOfWeek.values().toList()
    )
}

class HabitDetailViewModelCompose @ViewModelInject constructor(
    @Assisted state: SavedStateHandle,
    private val habitsRepository: HabitsRepository,
    private val daysRepository: DaysRepository
) : BaseComposeViewModel<HabitDetailStateCompose>(HabitDetailStateCompose(state.getArgs())) {

    private val args = state.getArgs<HabitDetailFragmentCompose.Args>()
    private val day = LocalDate.ofEpochDay(args.day.toLong())

    init {
        viewModelScope.launch {
            setState {
                copy(days = daysRepository.getDaysOfWeekSorted())
            }
        }

        viewModelScope.launch {
            habitsRepository.getHabitDetail(args.id)
                .collectAndSetState {
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
    }

    fun monthSelected(calendarMonth: CalendarMonth) = viewModelScope.launchSetState {
        copy(selectedMonth = calendarMonth.yearMonth)
    }

    fun toggleHabitCompletion(day: LocalDate) = viewModelScope.withState {
        val habit = it.habitDetail() ?: return@withState
        val epochDay = day.toEpochDay().toInt()
        habitsRepository.setHabitComplete(
            it.args.id,
            epochDay,
            habit.days.contains(epochDay.toLong()).not()
        )
    }

    fun delete() {
        habitsRepository.deleteHabit(args.id)
        NavigationEvent.back().publish()
    }

    fun edit() {
        NavigationEvent(
            R.id.createFragment,
            CreateFragment.Args(args.id).toBundle()
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

}