package com.janbina.habits.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.chibatching.kotpref.KotprefModel
import com.janbina.habits.R
import com.janbina.habits.data.ApiLevels
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    @ApplicationContext context: Context
) : KotprefModel(context), SharedPreferences.OnSharedPreferenceChangeListener {

    override val kotprefName = "preferences"

    private val listeners = HashMap<String, MutableList<() -> Unit>>()

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        listeners[key]?.forEach { it() }
    }

    fun addListener(key: String, listener: () -> Unit) {
        listeners.getOrPut(key, { mutableListOf() }).add(listener)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    var firstDayOfWeek by stringPref(
        default = FirstDayOfWeek.SYSTEM.value,
        key = KEY_FIRST_DAY_OF_WEEK
    )

    var theme by stringPref(
        default = Theme.SYSTEM.value,
        key = KEY_THEME
    )

    enum class FirstDayOfWeek(
        @StringRes val displayName: Int,
        val value: String
    ) {
        SATURDAY(R.string.pref_first_day_saturday, "sat"),
        SUNDAY(R.string.pref_first_day_sunday, "sun"),
        MONDAY(R.string.pref_first_day_monday, "mon"),
        SYSTEM(R.string.pref_first_day_system, "sys");

        companion object {
            fun find(value: String) = values().find { it.value == value } ?: SYSTEM
        }
    }

    internal enum class Theme(
        @StringRes val displayName: Int,
        val value: String
    ) {
        LIGHT(R.string.pref_theme_light, "light"),
        DARK(R.string.pref_theme_dark, "dark"),
        SYSTEM(
            if (ApiLevels.supportsSystemNightMode) {
                R.string.pref_theme_system
            } else {
                R.string.pref_theme_battery
            },
            "sys"
        );

        companion object {
            fun find(value: String) = values().find { it.value == value } ?: SYSTEM
        }
    }

    companion object {
        const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
        const val KEY_THEME = "theme"
    }
}