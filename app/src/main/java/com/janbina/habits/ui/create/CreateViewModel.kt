package com.janbina.habits.ui.create

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.Result
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseComposeViewModel
import com.janbina.habits.ui.base.getArgs
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.launch

data class CreateState(
    val name: String = ""
)

class CreateViewModelCompose @ViewModelInject constructor(
    @Assisted state: SavedStateHandle,
    private val habitsRepository: HabitsRepository
) : BaseComposeViewModel<CreateState>(CreateState()) {

    private val args = state.getArgs<CreateFragment.Args>()

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