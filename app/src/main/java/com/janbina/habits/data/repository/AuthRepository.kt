package com.janbina.habits.data.repository

import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.janbina.habits.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    private var user: User? = auth.currentUser?.let { User.fromFirebase(it) }
    private val _userFlow = MutableStateFlow(user)
    val userFlow: StateFlow<User?> get() = _userFlow

    private fun setUser(newUser: User?) {
        user = newUser
        _userFlow.value = newUser
    }

    fun getUser() = user

    suspend fun loginAnonymously(): Res<User> {
        return try {
            val result = auth.signInAnonymously().await()
            val newUser = User.fromFirebase(result.user!!)
            setUser(newUser)
            Result.Success(newUser)
        } catch(ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Res<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val current = auth.currentUser
            val newUser = if (current != null) {
                current.linkWithCredential(credential).await().user!!
            } else {
                auth.signInWithCredential(credential).await().user!!
            }.let { User.fromFirebase(it) }
            setUser(newUser)
            Result.Success(newUser)
        } catch(ex: Exception) {
            Result.Failure(ex)
        }
    }

    fun logout() {
        auth.signOut()
        setUser(null)
    }
}