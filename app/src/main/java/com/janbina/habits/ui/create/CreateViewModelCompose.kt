package com.janbina.habits.ui.create

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.success
import com.janbina.habits.R
import com.janbina.habits.data.repository.DaysRepository
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseComposeViewModel
import com.janbina.habits.ui.base.getArgs
import com.janbina.habits.ui.detail.HabitDetailFragment
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class CreateStateCompose(
    val name: String = ""
)

class CreateViewModelCompose @ViewModelInject constructor(
    @Assisted state: SavedStateHandle,
    private val habitsRepository: HabitsRepository
) : BaseComposeViewModel<CreateStateCompose>(CreateStateCompose()) {

    private val args = state.getArgs<CreateFragmentCompose.Args>()

    init {
        loadHabit()
    }

    fun nameChanged(name: String) = viewModelScope.launchSetState {
        copy(name = name)
    }

    fun save() = viewModelScope.withState {
        habitsRepository.saveHabit(args.id, it.name)
        NavigationEvent.back().publish()
    }

    private fun loadHabit() = viewModelScope.launch {
        args.id ?: return@launch
        val habit = habitsRepository.getHabitInfo(args.id)
        if (habit is Result.Success) {
            setState { copy(name = habit.value.name) }
        }
    }
}