package com.janbina.habits.ui.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.janbina.habits.data.repository.HabitsRepository

class CreateViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) :ViewModel() {

    fun createHabit(name: String) {
        habitsRepository.createHabit(name)
    }

}