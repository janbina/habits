package com.janbina.habits.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.janbina.habits.models.HabitDay
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.data.repository.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DayViewModel @ViewModelInject constructor(
    private val habitsRepository: HabitsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val day = savedStateHandle.get<Int>(DayFragment.ARG_DAY) ?: 0

    val habitsLv = liveData(Dispatchers.IO) {
//        emit(Resource.Loading())
        try{
            habitsRepository.getHabitsForDay(day).collect {
                emit(it)
            }
        }catch (e: Exception){
            emit(Res.error(e))
        }
    }

    fun markHabitAsCompleted(habit: HabitDay, completed: Boolean) {
        viewModelScope.launch {
            habitsRepository.setHabitComplete(habit.id, day.toString(), completed)
        }
    }

}