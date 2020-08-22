package com.janbina.habits.ui.create

import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.databinding.FragmentCreateBinding
import com.janbina.habits.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    private val viewModel: CreateViewModel by fragmentViewModel()

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        saveButton.setOnClickListener {
            viewModel.createHabit(habitName.text.toString())
        }
    }

    override fun invalidate() {}

}