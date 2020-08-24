package com.janbina.habits.data

import androidx.appcompat.app.AppCompatDelegate
import com.janbina.habits.data.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeProvider @Inject constructor(
    private val preferences: Preferences
) {

    fun getCurrentTheme(): Int {
        return when(Preferences.Theme.find(preferences.theme)) {
            Preferences.Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Preferences.Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Preferences.Theme.SYSTEM -> if (ApiLevels.supportsSystemNightMode) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            } else {
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
        }
    }

    fun setOnThemeChangeListener(action: (Int) -> Unit) {
        preferences.addListener(Preferences.KEY_THEME) {
            action(getCurrentTheme())
        }
    }

}