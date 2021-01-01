package com.janbina.habits.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.janbina.habits.BuildConfig
import com.janbina.habits.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGoogleSingIn()

        loginViewModel.onEachEvent<LoginViewModel.GoogleLoginEvent> {
            signInWithGoogle()
        }.launchIn(lifecycleScope)
    }

    private fun initGoogleSingIn() {
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                .requestEmail()
                .build()
        )
    }

    private fun signInWithGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val token = account?.idToken

            if (token != null) {
                loginViewModel.googleSignInSucceeded(token)
            } else {
                loginViewModel.googleSignInFailed(IllegalStateException("Token is null"))
            }
        } catch (e: ApiException) {
            loginViewModel.googleSignInFailed(e)
        }
    }

    companion object {
        const val RC_SIGN_IN = 10500
    }
}