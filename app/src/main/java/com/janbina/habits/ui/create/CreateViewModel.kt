package com.janbina.habits.ui.create

import com.airbnb.mvrx.MvRxState
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

data class CreateState(val x: String = "") : MvRxState

class CreateViewModel @AssistedInject constructor(
    @Assisted state: CreateState,
    private val habitsRepository: HabitsRepository
) : BaseViewModel<CreateState>(state) {

    fun createHabit(name: String) {
        habitsRepository.createHabit(name)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<CreateViewModel, CreateState> {
        override fun create(state: CreateState): CreateViewModel
    }

    companion object :
        DaggerVmFactory<CreateViewModel, CreateState>(CreateViewModel::class.java)

}