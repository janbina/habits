package com.janbina.habits.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()
}