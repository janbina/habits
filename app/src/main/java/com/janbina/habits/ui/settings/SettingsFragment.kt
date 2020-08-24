package com.janbina.habits.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.chibatching.kotpref.preference.dsl.PreferenceScreenBuilder
import com.chibatching.kotpref.preference.dsl.kotprefScreen
import com.janbina.habits.R
import com.janbina.habits.data.preferences.Preferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KProperty0

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var preferences: Preferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        kotprefScreen(preferences) {
            list2(it::firstDayOfWeek, getString(R.string.pref_first_day_title)) {
                entries = Preferences.FirstDayOfWeek.values()
                    .map { getString(it.displayName) }
                    .toTypedArray()
                entryValues = Preferences.FirstDayOfWeek.values()
                    .map { it.value }
                    .toTypedArray()
            }

            list2(it::theme, getString(R.string.pref_theme_title)) {
                entries = Preferences.Theme.values()
                    .map { getString(it.displayName) }
                    .toTypedArray()
                entryValues = Preferences.Theme.values()
                    .map { it.value }
                    .toTypedArray()
            }
        }
    }

    private fun PreferenceScreenBuilder.list2(
        property: KProperty0<String>,
        title: String,
        options: (ListPreference.() -> Unit)? = null
    ) = list(property, title) {
        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        dialogTitle = title
        options?.invoke(this)
    }
}
