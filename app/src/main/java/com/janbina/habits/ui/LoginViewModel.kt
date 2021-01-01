package com.janbina.habits.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.models.User
import com.janbina.habits.ui.base.BaseReduxVM
import com.janbina.habits.ui.viewevent.ViewEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class LoginState(
    val user: User? = null,
    val inProgress: Boolean = false
) {
    val loggedIn get() = user != null
}

class LoginViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
) : BaseReduxVM<LoginState>(LoginState()) {

    init {
        authRepository.userFlow
            .onEach { setState { copy(user = it) } }
            .launchIn(viewModelScope)
    }

    fun loginAnonymously() {
        viewModelScope.launch {
            setState { copy(inProgress = true) }
            val result = authRepository.loginAnonymously()
            result.fold(
                {
                    setState { copy(user = it, inProgress = false) }
                    SignInFinishedEvent.Success().publish()
                },
                {
                    setState { copy(user = null, inProgress = false) }
                    SignInFinishedEvent.Error(it).publish()
                }
            )
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launchSetState { copy(inProgress = true) }
        GoogleLoginEvent().publish()
    }

    fun logout() {
        authRepository.logout()
        LoggedOutEvent().publish()
    }

    fun googleSignInSucceeded(token: String) {
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(token)
            result.fold(
                {
                    setState { copy(user = it, inProgress = false) }
                    SignInFinishedEvent.Success().publish()
                },
                {
                    setState { copy(user = null, inProgress = false) }
                    SignInFinishedEvent.Error(it).publish()
                }
            )
        }
    }

    fun googleSignInFailed(error: Exception) {
        viewModelScope.launchSetState { copy(user = null, inProgress = false) }
        SignInFinishedEvent.Error(error).publish()
    }

    class GoogleLoginEvent : ViewEvent()
    sealed class SignInFinishedEvent : ViewEvent() {
        class Success : SignInFinishedEvent()
        class Error(error: Exception) : SignInFinishedEvent()
    }
    class LoggedOutEvent : ViewEvent()
}