package com.janbina.habits.ui.home

import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.janbina.habits.databinding.FragmentDayBinding
import com.janbina.habits.models.SimpleItem
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.ui.base.FragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class DayFragment : BaseFragment<FragmentDayBinding>(FragmentDayBinding::inflate) {

    private val viewModel: DayViewModel by fragmentViewModel()

    override fun setupView() {}

    override fun setupRegistrations() {
        handleNavigationEvents(viewModel)
    }

    override fun invalidate() = withState(viewModel) {
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

    @Parcelize
    data class Args(
        val day: Int,
    ) : FragmentArgs()

    companion object {
        fun create(args: Args) = DayFragment().apply { arguments = args.toBundle() }
    }
}

