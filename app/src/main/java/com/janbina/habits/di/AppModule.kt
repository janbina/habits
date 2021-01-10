package com.janbina.habits.di

import android.content.Context
import android.content.res.Resources
import com.janbina.habits.data.database.Database
import com.janbina.habits.data.database.FirestoreDb
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources
}