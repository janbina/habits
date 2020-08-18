package com.janbina.habits.repository

import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun currentUser() = auth.currentUser!!

    suspend fun loginAnonymously(): SuspendableResult<FirebaseUser, Exception> {
        return SuspendableResult.of {
            auth.signInAnonymously().await().user!!
        }
    }

    suspend fun loginWithGoogle(idToken: String): SuspendableResult<FirebaseUser, Exception> {
        return SuspendableResult.of {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await().user!!
        }
    }

}