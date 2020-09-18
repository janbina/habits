package com.janbina.habits.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.chibatching.kotpref.preference.dsl.PreferenceScreenBuilder
import com.chibatching.kotpref.preference.dsl.kotprefScreen
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.janbina.habits.BuildConfig
import com.janbina.habits.R
import com.janbina.habits.data.preferences.Preferences
import com.janbina.habits.databinding.FragmentSettingsBinding
import com.janbina.habits.models.User
import com.janbina.habits.ui.LoginViewModel
import com.janbina.habits.ui.base.BasePreferenceFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.reflect.KProperty0

@AndroidEntryPoint
class SettingsFragment :
    BasePreferenceFragment<FragmentSettingsBinding>(FragmentSettingsBinding::bind) {

    private val loginViewModel: LoginViewModel by existingViewModel()

    @Inject
    lateinit var preferences: Preferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        kotprefScreen(preferences) {
            withState(loginViewModel) { state ->
                if (state.user != null) {
                    preference(KEY_LOGGED_USER, "User") {
                        summary = createUserSummary(state.user)
                    }
                }
            }
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
            category("About") {
                val versionName = if (BuildConfig.BUILD_TYPE == "release") {
                    BuildConfig.VERSION_NAME
                } else {
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE})"
                }
                preference(KEY_VERSION, "Version") {
                    summary = versionName
                }
            }
        }
    }

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        findPreference<Preference>(KEY_LOGGED_USER)?.setOnPreferenceClickListener {
            logout()
            true
        }
        Unit
    }

    override fun invalidate() = withState(loginViewModel) {
        val user = it.user
        findPreference<Preference>(KEY_LOGGED_USER)?.apply {
            isVisible = user != null
            summary = createUserSummary(user)
        }
        Unit
    }

    private fun logout() {
        val user = withState(loginViewModel) { it.user } ?: return
        MaterialAlertDialogBuilder(requireContext(), R.style.DeleteAlertDialog)
            .setTitle("Logged in as ${user.name}")
            .setMessage("Do you want to log out?.")
            .setPositiveButton("Log out") { _, _ ->
                loginViewModel.logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createUserSummary(user: User?): String {
        return if (user == null) {
            "Logged out"
        } else {
            "${user.name} (${user.email})"
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

    companion object {
        private const val KEY_LOGGED_USER = "logged_user"
        private const val KEY_VERSION = "version"
    }
}
