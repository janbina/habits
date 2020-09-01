package com.janbina.habits.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.model.DayOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment : BaseComposeFragment() {

    private val viewModel: HabitDetailViewModel by viewModels()

    @Inject
    lateinit var dateFormatters: DateFormatters

    override fun invalidate() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                HabitDetailScreen(
                    dateFormatters = dateFormatters,
                    navController = findNavController(),
                    binder = dayBinder
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handleNavigationEvents()
    }

    private val dayBinder = BindingDayBinder(ItemCalendarDayDetailBinding::bind) { day ->
        val epochDay = day.date.toEpochDay()
        dayText.text = dateFormatters.dayNumFormatter.format(day.date)

        dayText.setOnLongClickListener {
            viewModel.toggleHabitCompletion(day.date)
            true
        }

        if (day.owner == DayOwner.THIS_MONTH) {
            this.dayText.setTextColor(Color.BLACK)
        } else {
            this.dayText.setTextColor(Color.GRAY)
        }

        if (viewModel.currentState().habitDetail()?.days?.contains(epochDay) == true) {
            background.isVisible = true
        } else {
            background.isVisible = false
        }
    }

    @Parcelize
    data class Args(
        val id: String,
        val day: Int
    ) : FragmentArgs()
}