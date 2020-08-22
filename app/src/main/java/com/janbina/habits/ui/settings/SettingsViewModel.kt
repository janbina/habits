package com.janbina.habits.ui.settings

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxState
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.ui.base.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

data class SettingsState(val x: String = "") : MvRxState

class SettingsViewModel @AssistedInject constructor(
    @Assisted state: SettingsState,
    private val authRepository: AuthRepository,
    private val habitsRepository: HabitsRepository
) : BaseViewModel<SettingsState>(state) {


    fun loginAnonymously() {
        viewModelScope.launch {
//            habitsRepository.createTask()
//            val user = authRepository.loginAnonymously()
//            user.success {
//                Timber.e("LOGGED IN: ${it.displayName} ${it.email}")
//            }
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<SettingsViewModel, SettingsState> {
        override fun create(state: SettingsState): SettingsViewModel
    }

    companion object :
        DaggerVmFactory<SettingsViewModel, SettingsState>(SettingsViewModel::class.java)

}