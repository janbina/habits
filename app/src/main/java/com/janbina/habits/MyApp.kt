package com.janbina.habits

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.janbina.habits.data.ThemeProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject lateinit var themeProvider: ThemeProvider

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(themeProvider.getCurrentTheme())

        themeProvider.setOnThemeChangeListener {
            AppCompatDelegate.setDefaultNightMode(it)
        }

        initTimber()
    }

    fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        } else {
//            Timber.plant(CrashReportingTree());
        }
    }
}