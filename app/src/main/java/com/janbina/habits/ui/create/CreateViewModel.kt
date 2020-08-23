package com.janbina.habits.ui.create

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxState
import com.github.kittinunf.result.success
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.viewevent.NavigationEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

data class CreateState(
    val args: CreateFragment.Args,
    val name: String
) : MvRxState {
    @Suppress("unused")
    constructor(args: CreateFragment.Args): this(args, "")
}

class CreateViewModel @AssistedInject constructor(
    @Assisted private val initialState: CreateState,
    private val habitsRepository: HabitsRepository
) : BaseViewModel<CreateState>(initialState) {

    init {
        loadHabit()
    }

    fun nameChanged(name: String) = setState {
        copy(name = name)
    }

    fun save() = withState {
        habitsRepository.saveHabit(initialState.args.id, it.name)
        NavigationEvent.back().publish()
    }

    private fun loadHabit() {
        val id = initialState.args.id ?: return
        viewModelScope.launch {
            habitsRepository.getHabitInfo(id).success {
                setState {
                    copy(name = it.name)
                }
            }
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<CreateViewModel, CreateState> {
        override fun create(initialState: CreateState): CreateViewModel
    }

    companion object :
        DaggerVmFactory<CreateViewModel, CreateState>(CreateViewModel::class.java)

}