package com.janbina.habits.di

import com.janbina.habits.di.helpers.AssistedViewModelFactory
import com.janbina.habits.di.helpers.ViewModelKey
import com.janbina.habits.ui.LoginViewModel
import com.janbina.habits.ui.detail.HabitDetailViewModel
import com.janbina.habits.ui.home.DayViewModel
import com.janbina.habits.ui.home.HomeViewModel
import com.janbina.habits.ui.settings.SettingsViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap

@AssistedModule
@Module(includes = [AssistedInject_ViewModelModule::class])
@DisableInstallInCheck
@InstallIn(ApplicationComponent::class)
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun settingsViewModelFactory(factory: SettingsViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun homeViewModelFactory(factory: HomeViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(DayViewModel::class)
    fun dayViewModelFactory(factory: DayViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun loginViewModelFactory(factory: LoginViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(HabitDetailViewModel::class)
    fun habitDetailViewModelFactory(factory: HabitDetailViewModel.Factory): AssistedViewModelFactory<*, *>
}