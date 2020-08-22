package com.janbina.habits.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.janbina.habits.R
import com.janbina.habits.data.repository.HabitsRepository
import com.janbina.habits.databinding.FragmentHabitDetailBinding
import com.janbina.habits.databinding.ItemCalendarDayDetailBinding
import com.janbina.habits.helpers.px
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.util.BindingDayBinder
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.utils.Size
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth

@AndroidEntryPoint
class HabitDetailFragment :
    BaseFragment<FragmentHabitDetailBinding>(FragmentHabitDetailBinding::inflate) {

    private val viewModel: HabitDetailViewModel by fragmentViewModel()

    override fun invalidate() = withState(viewModel) {
        binding.calendar.scrollToMonth(it.selectedMonth)
        binding.calendarMonth.text = it.selectedMonth.month.toString()
        when (it.habitDetail) {
            is Incomplete -> Unit
            is Success -> {
                binding.name.text = it.habitDetail()?.habit?.name
                binding.calendar.notifyCalendarChanged()
            }
            is Fail -> Unit
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        binding.calendar.daySize =
            Size.autoWidth(resources.getDimension(R.dimen.habit_detail_day_height).toInt())
        binding.calendar.setMonthPadding(16.px, 0, 16.px, 0)

        val startMonth = YearMonth.of(1970, Month.JANUARY)
        val endMonth = YearMonth.of(2200, Month.DECEMBER)
        binding.calendar.setup(startMonth, endMonth, DayOfWeek.SUNDAY)

        binding.calendar.dayBinder =
            BindingDayBinder(ItemCalendarDayDetailBinding::bind) { day ->
                val epochDay = day.date.toEpochDay()
                dayText.text = day.date.dayOfMonth.toString()

                dayText.setOnClickListener {
                    Timber.e("CLICKED")
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

        binding.calendar.monthScrollListener = {
            viewModel.monthSelected(it.yearMonth)
        }

        populateLegend()
    }

    fun populateLegend() {
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEachIndexed { index, name ->
            View.inflate(requireContext(), R.layout.item_weekday_day_detail, binding.legendLayout)
            (binding.legendLayout.getChildAt(index) as TextView).text = name
        }
    }

    private fun setupView() = binding.apply {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_delete -> {
                    viewModel.delete()
                    findNavController().navigateUp()
                    true
                }
                else -> false
            }
        }
    }

    @Parcelize
    data class Args(
        val id: String,
        val day: Int
    ) : FragmentArgs()
}