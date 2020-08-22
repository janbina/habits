package com.janbina.habits.ui.create

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.databinding.FragmentCreateBinding
import com.janbina.habits.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    private val viewModel: CreateViewModel by fragmentViewModel()

    override fun invalidate() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        val et = binding.habitName
        binding.saveButton.setOnClickListener {
            viewModel.createHabit(et.text.toString())
        }
    }

    private fun setupViews() = binding.apply {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}