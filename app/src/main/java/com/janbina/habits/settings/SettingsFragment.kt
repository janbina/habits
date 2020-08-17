package com.janbina.habits.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.janbina.habits.DateFormatters
import com.janbina.habits.HomeViewModel
import com.janbina.habits.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

}