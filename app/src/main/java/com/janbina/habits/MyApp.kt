package com.janbina.habits

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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