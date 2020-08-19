package com.janbina.habits.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.janbina.habits.data.repository.HabitsRepository
import java.time.LocalDate

class HomeViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state: LiveData<State> get() = _state

    fun dateChanged(newDate: LocalDate) {
        _state.value = _state.value!!.copy(selectedDate = newDate)
    }

    data class State(
        val selectedDate: LocalDate = LocalDate.now()
    )
}