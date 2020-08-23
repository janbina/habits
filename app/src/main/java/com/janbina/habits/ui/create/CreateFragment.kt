package com.janbina.habits.ui.create

import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.janbina.habits.databinding.FragmentCreateBinding
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    private val viewModel: CreateViewModel by fragmentViewModel()

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        habitName.doAfterTextChanged { viewModel.nameChanged(habitName.text.toString()) }
        saveButton.setOnClickListener { viewModel.save() }

        habitName.showImeDelayed()
    }

    override fun setupRegistrations() {
        handleNavigationEvents(viewModel)
    }

    override fun invalidate() = withState(viewModel) {
        binding.habitName.updateText(it.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideIme()
    }

    @Parcelize
    data class Args(
        val id: String? = null
    ) : FragmentArgs()
}