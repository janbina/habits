package com.janbina.habits.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.*
import com.janbina.habits.R
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.helpers.px
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.ui.compose.DateFormatterAmbient
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import java.time.DayOfWeek
import java.time.YearMonth
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragmentCompose : BaseComposeFragment() {

    private val viewModel: HabitDetailViewModelCompose by viewModels()

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
                Providers(
                    DateFormatterAmbient provides dateFormatters
                ) {
                    MaterialTheme {
                        val viewState by viewModel.liveData.observeAsState()
                        viewState?.let {
                            Column {
                                TopAppBar {}
                                DayLegend(it)
                                Calendar(it, dayBinder, viewModel::monthSelected)
                            }
                        }
                    }
                }
            }
        }
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