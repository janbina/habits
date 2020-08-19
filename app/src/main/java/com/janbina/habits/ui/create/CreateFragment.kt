package com.janbina.habits.ui.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.janbina.habits.databinding.FragmentCreateBinding
import com.janbina.habits.ui.base.ViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFragment : ViewBindingFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    private val viewModel: CreateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val et = binding.habitName
        binding.saveButton.setOnClickListener {
            viewModel.createHabit(et.text.toString())
        }
    }

}