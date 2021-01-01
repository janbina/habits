package com.janbina.habits.ui.home

import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.janbina.habits.databinding.FragmentDayBinding
import com.janbina.habits.models.SimpleItem
import com.janbina.habits.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class DayFragment : BaseFragment<FragmentDayBinding>(FragmentDayBinding::inflate) {

    private val viewModel: DayViewModel by viewModels()

    override fun setupRegistrations() {
        viewModel.handleNavigationEvents()

        viewModel.liveData.observe(viewLifecycleOwner) {
            it.habits()?.let {
                binding.recycler.withModels {
                    it.forEach { habit ->
                        SimpleItem(
                            habit,
                            { completed -> viewModel.markHabitAsCompleted(habit, completed) },
                            { viewModel.openHabit(habit) }
                        ).id(habit.id).addTo(this)
                    }
                }
            } ?: Unit
        }
    }

    @Parcelize
    data class Args(
        val day: Int,
    ) : Parcelable

    companion object {
        fun create(args: Args) = DayFragment().apply { arguments = bundleOf() }
    }
}

