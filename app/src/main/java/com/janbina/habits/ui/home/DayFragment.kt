package com.janbina.habits.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.janbina.habits.R
import com.janbina.habits.databinding.FragmentDayBinding
import com.janbina.habits.helpers.argOr
import com.janbina.habits.models.SimpleItem
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.ui.detail.HabitDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class DayFragment : BaseFragment<FragmentDayBinding>(FragmentDayBinding::inflate) {

    private val viewModel: DayViewModel by fragmentViewModel()
    private val args: Args by args()

    override fun setupView() {}

    override fun invalidate() = withState(viewModel) {
        it.habits()?.let {
            binding.recycler.withModels {
                it.forEach { habit ->
                    SimpleItem(
                        habit,
                        { completed -> viewModel.markHabitAsCompleted(habit, completed) },
                        {
                            findNavController().navigate(
                                R.id.habitDetailFragment, HabitDetailFragment.Args(
                                    habit.id, args.day
                                ).toBundle()
                            )
                        }
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

