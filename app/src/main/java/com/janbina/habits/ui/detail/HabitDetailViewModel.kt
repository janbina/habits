package com.janbina.habits.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.janbina.habits.data.repository.HabitsRepository

class HabitDetailViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) : ViewModel() {



}