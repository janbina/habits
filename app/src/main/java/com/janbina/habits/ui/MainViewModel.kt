package com.janbina.habits.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.coroutines.success
import com.janbina.habits.data.repository.AuthRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun googleSignInSucceeded(token: String) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(token).success {
                Timber.e("USER SIGNED IN!! ${it.email}")
            }
        }
    }

    fun googleSignInFailed(statusCode: Int) {
        Timber.e("GOOGLE SIGN IN FAILED")
    }



}