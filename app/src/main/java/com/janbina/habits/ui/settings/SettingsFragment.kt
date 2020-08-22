package com.janbina.habits.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.R
import com.janbina.habits.databinding.FragmentSettingsBinding
import com.janbina.habits.ui.MainActivity
import com.janbina.habits.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by fragmentViewModel()

    override fun invalidate() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            (activity as MainActivity).signIn()
            //viewModel.loginAnonymously()
        }
    }

    private fun setupViews() = binding.apply {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}