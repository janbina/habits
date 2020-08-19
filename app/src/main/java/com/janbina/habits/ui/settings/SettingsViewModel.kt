package com.janbina.habits.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.data.repository.HabitsRepository
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    private val habitsRepository: HabitsRepository
): ViewModel() {


    fun loginAnonymously() {
        viewModelScope.launch {
            habitsRepository.createTask()
//            val user = authRepository.loginAnonymously()
//            user.success {
//                Timber.e("LOGGED IN: ${it.displayName} ${it.email}")
//            }
        }
    }

}