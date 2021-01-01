package com.janbina.habits.ui.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.Result
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.launch

data class CreateState(
    val id: String?,
    val name: String = ""
)

class CreateViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) : BaseReduxVM<CreateState>(CreateState(null)) {

    private val id: String? = null

    init {
        loadHabit()
    }

    fun nameChanged(name: String) = viewModelScope.launchSetState {
        copy(name = name)
    }

    fun save() = viewModelScope.launch {
        withState {
            habitsRepository.saveHabit(id, it.name)
            NavigationEvent.back().publish()
        }
    }

    private fun loadHabit() = viewModelScope.launch {
        id ?: return@launch
        val habit = habitsRepository.getHabitInfo(id)
        if (habit is Result.Success) {
            setState { copy(name = habit.value.name) }
        }
    }
}