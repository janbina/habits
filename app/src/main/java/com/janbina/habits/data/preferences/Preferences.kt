package com.janbina.habits.data.preferences

import android.content.Context
import androidx.annotation.StringRes
import com.chibatching.kotpref.KotprefModel
import com.janbina.habits.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    @ApplicationContext context: Context
) : KotprefModel(context) {
    override val kotprefName = "preferences"

    var firstDayOfWeek by stringPref(
        default = FirstDayOfWeek.SYSTEM.value,
        key = KEY_FIRST_DAY_OF_WEEK
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

    companion object {
        const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
    }
}