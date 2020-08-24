package com.janbina.habits.ui.detail

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.janbina.habits.R
import com.janbina.habits.databinding.FragmentHabitDetailBinding
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.helpers.px
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.util.BindingDayBinder
import com.janbina.habits.util.setMenuActions
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.utils.Size
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class HabitDetailFragment :
    BaseFragment<FragmentHabitDetailBinding>(FragmentHabitDetailBinding::inflate) {

    private val viewModel: HabitDetailViewModel by fragmentViewModel()
    @Inject
    lateinit var dateFormatters: DateFormatters

    override fun invalidate() = withState(viewModel) {
        binding.calendarMonth.text = dateFormatters.formatMonthNameOptionalYear(it.selectedMonth)
        when (it.habitDetail) {
            is Incomplete -> Unit
            is Success -> {
                binding.name.text = it.habitDetail()?.habit?.name
                binding.calendar.layoutManager?.let {
                    binding.calendar.notifyCalendarChanged()
                }
            }
            is Fail -> Unit
        }

        binding.legendLayout.children.filterIsInstance(TextView::class.java)
            .forEachIndexed { index, view ->
                view.text =
                    it.days.getOrNull(index)?.let { dateFormatters.shortDayNameFormatter.format(it) }
                        ?: ""
            }

        binding.calendar.setup(it.startMonth, it.endMonth, it.days.first())
        binding.calendar.scrollToMonth(it.selectedMonth)
        binding.calendar.monthScrollListener = viewModel::monthSelected
    }

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        toolbar.setMenuActions(
            mapOf(
                R.id.menu_item_delete to ::confirmDeletion,
                R.id.menu_item_edit to viewModel::edit
            )
        )

        calendar.daySize = Size.autoWidth(resources.getDimension(R.dimen.habit_detail_day_height).toInt())
        binding.calendar.setMonthPadding(16.px, 0, 16.px, 0)

        calendar.dayBinder = BindingDayBinder(ItemCalendarDayDetailBinding::bind) { day ->
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

        repeat(7) {
            View.inflate(requireContext(), R.layout.item_weekday_day_detail, legendLayout)
        }
    }

    override fun setupRegistrations() {
        handleNavigationEvents(viewModel)
    }

    private fun confirmDeletion() {
        MaterialAlertDialogBuilder(requireContext(), R.style.DeleteAlertDialog)
            .setTitle("Delete ${withState(viewModel) { it.habitDetail()?.habit?.name }}")
            .setMessage("Are you sure you want to delete this habit? You will lose all the history and it cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @Parcelize
    data class Args(
        val id: String,
        val day: Int
    ) : FragmentArgs()
}