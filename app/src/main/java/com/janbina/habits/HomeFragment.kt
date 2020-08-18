package com.janbina.habits

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var dateFormatters: DateFormatters
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var calendar: com.kizitonwose.calendarview.CalendarView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)

        viewModel.state.observe(viewLifecycleOwner, ::onStateChanged)
    }

    fun dateSelected(date: Int) {
        viewModel.dateChanged(LocalDate.ofEpochDay(date.toLong()))
    }

    private fun setupViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        viewPager = view.findViewById(R.id.viewPager)
        calendar = view.findViewById(R.id.exSevenCalendar)

        val et = view.findViewById<EditText>(R.id.habitName)
        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            viewModel.createHabit(et.text.toString())
        }


        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = Int.MAX_VALUE

            override fun createFragment(position: Int) = DayFragment.create(position)
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dateSelected(position)
            }
        })

        val today = LocalDate.now()

        calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(dateFormatters, viewModel, view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        calendar.setup(today.yearMonth.minusMonths(1), today.yearMonth, DayOfWeek.MONDAY)
        calendar.scrollToDate(today.minusDays(4))

        calendar.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING
            }
        })
    }

    private fun onStateChanged(state: HomeViewModel.State) {
        viewPager.setCurrentItem(state.selectedDate.toEpochDay().toInt(), true)
        toolbar.title = dateFormatters.formatRelative(state.selectedDate)
        calendar.notifyCalendarChanged()
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
            dayNum.setTextColor(ContextCompat.getColor(dayNum.context, R.color.example_3_blue))
            dayNum.setBackgroundResource(R.drawable.day_selected)
        } else {
            dayNum.setTextColor(Color.WHITE)
            dayNum.background = null
        }
    }
}
