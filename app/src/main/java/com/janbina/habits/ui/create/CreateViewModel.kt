package com.janbina.habits.ui.create

import com.airbnb.mvrx.MvRxState
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

data class CreateState(
    val name: String = ""
) : MvRxState

class CreateViewModel @AssistedInject constructor(
    @Assisted initialState: CreateState,
    private val habitsRepository: HabitsRepository
) : BaseViewModel<CreateState>(initialState) {

    fun nameChanged(name: String) = setState {
        copy(name = name)
    }

    fun createHabit() = withState {
        habitsRepository.createHabit(it.name)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<CreateViewModel, CreateState> {
        override fun create(initialState: CreateState): CreateViewModel
    }

    companion object :
        DaggerVmFactory<CreateViewModel, CreateState>(CreateViewModel::class.java)

}