package com.janbina.habits.ui.home

import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.janbina.habits.R
import com.janbina.habits.databinding.Example7DayBinding
import com.janbina.habits.databinding.FragmentHomeBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.helpers.hide
import com.janbina.habits.helpers.px
import com.janbina.habits.helpers.show
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.util.BindingDayBinder
import com.janbina.habits.util.disableScroll
import com.janbina.habits.util.onPageSelected
import com.janbina.habits.util.setMenuActions
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var dateFormatters: DateFormatters
    private val viewModel: HomeViewModel by fragmentViewModel()

    override fun invalidate() = withState(viewModel) {
        binding.viewPager.setCurrentItem(
            it.selectedDate.toEpochDay().toInt(),
            binding.viewPager.currentItem != 0
        )
        binding.toolbar.title = dateFormatters.formatRelative(it.selectedDate)
        binding.calendar.notifyCalendarChanged()
    }

    override fun setupRegistrations() {
        handleNavigationEvents(viewModel)
    }

    override fun setupView() = with(binding) {

        toolbar.setMenuActions(mapOf(
            R.id.menu_item_settings to viewModel::goToSettings,
            R.id.menu_item_create to viewModel::goToHabitCreation
        ))

        viewPager.adapter = ViewPagerAdapter(this@HomeFragment)
        viewPager.onPageSelected {
            if (it != 0) {
                dateSelected(it)
            }
        }

        val today = LocalDate.now()

        calendar.dayBinder = BindingDayBinder(Example7DayBinding::bind) { day ->
            root.setOnClickListener {
                viewModel.dateChanged(day.date)
            }
            dayNum.text = dateFormatters.dayNumFormatter.format(day.date)
            dayName.text = dateFormatters.dayNameFormatter.format(day.date)

            withState(viewModel) {
                if (day.date.isEqual(it.selectedDate)) {
                    dayNum.setTextColor(
                        ContextCompat.getColor(
                            dayNum.context,
                            R.color.example_3_blue
                        )
                    )
                    background.show()
                } else {
                    dayNum.setTextColor(Color.WHITE)
                    background.hide()
                }
            }
        }

        calendar.daySize = Size.autoWidth(60.px)

        calendar.setup(today.yearMonth.minusMonths(1), today.yearMonth.plusMonths(1), DayOfWeek.MONDAY)
        calendar.scrollToDate(today.minusDays(4))

        calendar.disableScroll()
    }

    private fun dateSelected(date: Int) {
        viewModel.dateChanged(LocalDate.ofEpochDay(date.toLong()))
    }
}

private class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = Int.MAX_VALUE
    override fun createFragment(position: Int) = DayFragment.create(DayFragment.Args(position))
}