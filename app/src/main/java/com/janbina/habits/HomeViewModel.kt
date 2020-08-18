package com.janbina.habits

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.janbina.habits.repository.HabitsRepository
import java.time.LocalDate

class HomeViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state: LiveData<State> get() = _state

    fun dateChanged(newDate: LocalDate) {
        _state.value = _state.value!!.copy(selectedDate = newDate)
    }

    fun createHabit(name: String) {
        habitsRepository.createHabit(name)
    }

    data class State(
        val selectedDate: LocalDate = LocalDate.now()
    )
}