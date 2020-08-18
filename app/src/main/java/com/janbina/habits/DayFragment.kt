package com.janbina.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.airbnb.epoxy.EpoxyRecyclerView
import com.github.kittinunf.result.success
import com.janbina.habits.databinding.FragmentDayBinding
import com.janbina.habits.databinding.FragmentHomeBinding
import com.janbina.habits.models.SimpleItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayFragment : ViewBindingFragment<FragmentDayBinding>(FragmentDayBinding::inflate) {

    private val viewModel: DayViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.habitsLv.observe(viewLifecycleOwner) {
            it.success {
                binding.recycler.withModels {
                    it.forEach { habit ->
                        SimpleItem(habit) { b -> viewModel.markHabitAsCompleted(habit, b)}.id(habit.id).addTo(this)
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_DAY = "ARG_DAY"

        fun create(daysSinceEpoch: Int) = DayFragment().apply {
            arguments = bundleOf(
                ARG_DAY to daysSinceEpoch
            )
        }
    }
}

