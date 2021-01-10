package com.janbina.habits.di

import com.janbina.habits.data.database.Database
import com.janbina.habits.data.database.FirestoreDb
import com.janbina.habits.data.database.MultiDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class BindingsModule {
    @Binds
    abstract fun providesDatabase(database: FirestoreDb): Database
}