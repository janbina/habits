package com.janbina.habits.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.coroutines.success
import com.janbina.habits.repository.AuthRepository
import com.janbina.habits.repository.HabitsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

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