package com.janbina.habits

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class HomeViewModel @ViewModelInject constructor(
    private val repo: SomeRepository
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