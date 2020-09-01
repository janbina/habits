package com.janbina.habits.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.janbina.habits.R
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.ui.compose.DateFormatterAmbient
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.model.DayOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment : BaseComposeFragment() {

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
                                HabitsAppBar(
                                    onNavIconPressed = findNavController()::navigateUp,
                                    actions = {
                                        IconButton(onClick = viewModel::edit) {
                                            Icon(asset = Icons.Filled.Edit)
                                        }
                                        IconButton(onClick = ::confirmDeletion) {
                                            Icon(asset = Icons.Filled.Delete)
                                        }
                                    })
                                HabitHeader(it)
                                DayLegend(it)
                                Calendar(it, dayBinder, viewModel::monthSelected)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handleNavigationEvents()
    }

    private fun confirmDeletion() {
        MaterialAlertDialogBuilder(requireContext(), R.style.DeleteAlertDialog)
            .setTitle("Delete ${viewModel.currentState().habitDetail()?.habit?.name}")
            .setMessage("Are you sure you want to delete this habit? You will lose all the history and it cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete()
            }
            .setNegativeButton("Cancel", null)
            .show()
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