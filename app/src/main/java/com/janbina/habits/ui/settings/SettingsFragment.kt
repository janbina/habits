package com.janbina.habits.ui.settings

import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.databinding.FragmentSettingsBinding
import com.janbina.habits.ui.MainActivity
import com.janbina.habits.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by fragmentViewModel()

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        loginButton.setOnClickListener {
            (activity as MainActivity).signIn()
            //viewModel.loginAnonymously()
        }
    }

    override fun invalidate() {}

}