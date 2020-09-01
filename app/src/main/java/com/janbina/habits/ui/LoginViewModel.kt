package com.janbina.habits.ui

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.github.kittinunf.result.coroutines.success
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.common.api.ApiException
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.DaggerVmFactory
import com.janbina.habits.models.User
import com.janbina.habits.ui.base.BaseViewModel
import com.janbina.habits.ui.viewevent.ViewEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

data class LoginState(
    val user: User? = null,
    val inProgress: Boolean = false
) : MvRxState {
    val loggedIn get() = user != null
}

class LoginViewModel @AssistedInject constructor(
    @Assisted initialState: LoginState,
    private val authRepository: AuthRepository,
) : BaseViewModel<LoginState>(initialState) {

    init {
        authRepository.userFlow
            .onEach { setState { copy(user = it) } }
            .launchIn(viewModelScope)
    }

    fun loginAnonymously() {
        viewModelScope.launch {
            setState { copy(inProgress = true) }
            val result = authRepository.loginAnonymously()
            result.success {
                setState { copy(user = it, inProgress = false) }
                SignInFinishedEvent.Success().publish()
            }
            result.failure {
                setState { copy(user = null, inProgress = false) }
                SignInFinishedEvent.Error(it).publish()
            }
        }
    }

    fun loginWithGoogle() {
        setState { copy(inProgress = true) }
        GoogleLoginEvent().publish()
    }

    fun logout() {
        authRepository.logout()
        LoggedOutEvent().publish()
    }

    fun googleSignInSucceeded(token: String) {
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(token)
            result.success {
                setState { copy(user = it, inProgress = false) }
                SignInFinishedEvent.Success().publish()
            }
            result.failure {
                setState { copy(user = null, inProgress = false) }
                SignInFinishedEvent.Error(it).publish()
            }
        }
    }

    fun googleSignInFailed(error: Exception) {
        setState { copy(user = null, inProgress = false) }
        SignInFinishedEvent.Error(error).publish()
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<LoginViewModel, LoginState> {
        override fun create(initialState: LoginState): LoginViewModel
    }

    companion object :
        DaggerVmFactory<LoginViewModel, LoginState>(LoginViewModel::class.java)

    class GoogleLoginEvent : ViewEvent()
    sealed class SignInFinishedEvent : ViewEvent() {
        class Success : SignInFinishedEvent()
        class Error(error: Exception) : SignInFinishedEvent()
    }
    class LoggedOutEvent : ViewEvent()
}