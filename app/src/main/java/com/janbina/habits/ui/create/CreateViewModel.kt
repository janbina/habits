package com.janbina.habits.ui.create

import com.airbnb.mvrx.MavericksState
import com.github.kittinunf.result.Result
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

data class CreateState(
    val id: String?,
    val name: String = ""
) : MavericksState {

    @Suppress("unused")
    constructor(args: CreateFragment.Args): this(
        id = args.id
    )
}

class CreateViewModel @AssistedInject constructor(
    @Assisted initialState: CreateState,
    private val habitsRepository: HabitsRepository
) : BaseViewModel<CreateState>(initialState) {

    private val id = initialState.id

    init {
        loadHabit()
    }

    fun nameChanged(name: String) = setState {
        copy(name = name)
    }

    fun save() = withState {
        habitsRepository.saveHabit(id, it.name)
        NavigationEvent.back().publish()
    }

    private fun loadHabit() = viewModelScope.launch {
        id ?: return@launch
        val habit = habitsRepository.getHabitInfo(id)
        if (habit is Result.Success) {
            setState { copy(name = habit.value.name) }
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<CreateViewModel, CreateState> {
        override fun create(initialState: CreateState): CreateViewModel
    }

    companion object :
        DaggerVmFactory<CreateViewModel, CreateState>(CreateViewModel::class.java)
}