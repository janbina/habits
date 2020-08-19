package com.janbina.habits.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.janbina.habits.databinding.FragmentHabitDetailBinding
import com.janbina.habits.ui.base.ViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HabitDetailFragment :
    ViewBindingFragment<FragmentHabitDetailBinding>(FragmentHabitDetailBinding::inflate) {

    private val viewModel: HabitDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}