package com.janbina.habits.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.janbina.habits.*
import com.janbina.habits.databinding.FragmentHomeBinding
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.ui.base.ViewBindingFragment
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var dateFormatters: DateFormatters
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        viewModel.state.observe(viewLifecycleOwner, ::onStateChanged)
    }

    fun dateSelected(date: Int) {
        viewModel.dateChanged(LocalDate.ofEpochDay(date.toLong()))
    }

    private fun setupViews() {

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_item_settings) {
                findNavController().navigate(HomeFragmentDirections.toSettingsFragment())
                true
            } else {
                false
            }
        }

        val et = binding.habitName
        binding.saveButton.setOnClickListener {
            viewModel.createHabit(et.text.toString())
        }


        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = Int.MAX_VALUE

            override fun createFragment(position: Int) = DayFragment.create(position)
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dateSelected(position)
            }
        })

        val today = LocalDate.now()

        binding.calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) =
                DayViewContainer(
                    dateFormatters,
                    viewModel,
                    view
                )
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        binding.calendar.setup(today.yearMonth.minusMonths(1), today.yearMonth, DayOfWeek.MONDAY)
        binding.calendar.scrollToDate(today.minusDays(4))

        binding.calendar.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING
            }
        })
    }

    private fun onStateChanged(state: HomeViewModel.State) {
        binding.viewPager.setCurrentItem(state.selectedDate.toEpochDay().toInt(), true)
        binding.toolbar.title = dateFormatters.formatRelative(state.selectedDate)
        binding.calendar.notifyCalendarChanged()
    }
}

class DayViewContainer(
    private val dateFormatters: DateFormatters,
    private val viewModel: HomeViewModel,
    view: View
) : ViewContainer(view) {

    lateinit var day: CalendarDay

    val dayNum = view.findViewById<TextView>(R.id.dayNum)
    val dayName = view.findViewById<TextView>(R.id.dayName)

    init {
        view.setOnClickListener {
            viewModel.dateChanged(day.date)
        }
    }

    fun bind(day: CalendarDay) {
        this.day = day
        dayNum.text = dateFormatters.dayNumFormatter.format(day.date)
        dayName.text = dateFormatters.dayNameFormatter.format(day.date)

        if (day.date.isEqual(viewModel.state.value?.selectedDate)) {
            dayNum.setTextColor(ContextCompat.getColor(dayNum.context,
                R.color.example_3_blue
            ))
            dayNum.setBackgroundResource(R.drawable.day_selected)
        } else {
            dayNum.setTextColor(Color.WHITE)
            dayNum.background = null
        }
    }
}
