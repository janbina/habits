package com.janbina.habits.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.chibatching.kotpref.preference.dsl.kotprefScreen
import com.janbina.habits.R
import com.janbina.habits.data.preferences.Preferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var preferences: Preferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        kotprefScreen(preferences) {
            list(it::firstDayOfWeek, getString(R.string.pref_first_day_title)) {
                summary = getString(
                    Preferences.FirstDayOfWeek
                        .find(it.firstDayOfWeek)
                        .displayName
                )
                entries =
                    Preferences.FirstDayOfWeek.values()
                        .map { getString(it.displayName) }
                        .toTypedArray()
                entryValues =
                    Preferences.FirstDayOfWeek.values()
                        .map { it.value }
                        .toTypedArray()
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                dialogTitle = getString(R.string.pref_first_day_title)
            }
        }
    }
}
