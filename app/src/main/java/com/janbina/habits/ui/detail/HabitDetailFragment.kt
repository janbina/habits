package com.janbina.habits.ui.detail

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.ui.base.BaseComposeMavericksFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.model.DayOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment : BaseComposeMavericksFragment() {

    @Inject
    lateinit var dateFormatters: DateFormatters

    private val viewModel: HabitDetailViewModel by fragmentViewModel()

    @Composable
    override fun content() = HabitDetailScreen(
        dateFormatters = dateFormatters,
        navController = findNavController(),
        binder = dayBinder
    )

    override fun invalidate() {}

    override fun setupRegistrations() {
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

        withState(viewModel) {
            if (it.habitDetail()?.days?.contains(epochDay) == true) {
                background.isVisible = true
            } else {
                background.isVisible = false
            }
        }
    }

    @Parcelize
    data class Args(
        val id: String,
        val day: Int
    ) : FragmentArgs()
}