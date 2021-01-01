package com.janbina.habits.ui.detail

import android.graphics.Color
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.model.DayOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment : BaseComposeFragment() {

    @Inject
    lateinit var dateFormatters: DateFormatters

    private val viewModel: HabitDetailViewModel by viewModels()

    @Composable
    override fun content() = HabitDetailScreen(
        dateFormatters = dateFormatters,
        navController = findNavController(),
        binder = dayBinder,
        viewModel = viewModel,
    )

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

        viewModel.currentState().let {
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
    ) : Parcelable
}