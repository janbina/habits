package com.janbina.habits.ui.detail

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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
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
    val days: List<DayOfWeek> = DayOfWeek.values().toList(),
    val habitEditationState: HabitEditationState = HabitEditationState(),
    val habitEditationVisible: Boolean = false,
)

class HabitDetailViewModel @AssistedInject constructor(
    @Assisted initialState: HabitDetailState,
    private val habitsRepository: HabitsRepository,
    private val daysRepository: DaysRepository
) : BaseReduxVM<HabitDetailState>(initialState) {

    private val id = initialState.id
    private val day = initialState.day

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
        viewModelScope.launchSetState {
            this.habitDetail()?.habit?.let {
                copy(habitEditationState = HabitEditationState(it.id, it.name), habitEditationVisible = true)
            } ?: this
        }
    }

    fun updateEdit(editationState: HabitEditationState) = viewModelScope.launchSetState {
        copy(habitEditationState = editationState)
    }

    fun cancelEdit() = viewModelScope.launchSetState {
        copy(habitEditationVisible = false)
    }

    fun saveEdit() = viewModelScope.launchSetState {
        if (!habitEditationVisible || !habitEditationState.isValid()) {
            this
        } else {
            habitsRepository.saveHabit(habitEditationState.id, habitEditationState.name)
            copy(habitEditationVisible = false)
        }
    }

    fun toggleArchived() = viewModelScope.withState {
        it.habitDetail()?.habit?.let { habit ->
            habitsRepository.saveHabit(habit.copy(archived = !habit.archived))
        }
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
    internal interface Factory {
        fun create(initialState: HabitDetailState): HabitDetailViewModel
    }
}

internal fun HabitDetailViewModel.Factory.create(
    id: String,
    day: LocalDate,
): HabitDetailViewModel {
    return create(HabitDetailState(id = id, day = day))
}