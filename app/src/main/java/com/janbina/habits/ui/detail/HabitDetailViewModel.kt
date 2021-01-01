package com.janbina.habits.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.Result
import com.janbina.habits.data.repository.DaysRepository
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.models.Async
import com.janbina.habits.models.Fail
import com.janbina.habits.models.Success
import com.janbina.habits.models.Uninitialized
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.launch
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
)

class HabitDetailViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository,
    private val daysRepository: DaysRepository
) : BaseReduxVM<HabitDetailState>(HabitDetailState("xxx", LocalDate.now())) {

    private val id = "xxx"
    private val day = LocalDate.now()

    init {
        viewModelScope.launchSetState {
            copy(
                selectedMonth = day.yearMonth,
                days = daysRepository.getDaysOfWeekSorted()
            )
        }

        viewModelScope.launch {
            habitsRepository.getHabitDetail(id).collectAndSetState {
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

    fun toggleHabitCompletion(day: LocalDate) = viewModelScope.launch {
        withState {
            val habit = it.habitDetail() ?: return@withState
            val epochDay = day.toEpochDay().toInt()
            habitsRepository.setHabitComplete(
                habit.habit.id,
                epochDay,
                habit.days.contains(epochDay.toLong()).not()
            )
        }
    }

    fun delete() {
        habitsRepository.deleteHabit(id)
        NavigationEvent.back().publish()
    }

    fun edit() {
//        NavigationEvent(
//            R.id.createFragment,
//            CreateFragment.Args(id).toBundle()
//        ).publish()
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