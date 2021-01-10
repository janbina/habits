package com.janbina.habits.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun providesFirebaseFirestore() = Firebase.firestore

    @Provides
    fun providesFirebaseRealtimeDb() = Firebase.database
}