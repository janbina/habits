package com.janbina.habits.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Incomplete
import com.airbnb.mvrx.Success
import com.janbina.habits.R
import com.janbina.habits.helpers.px
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder

@Composable
fun Calendar(state: HabitDetailStateCompose, binder: DayBinder<*>, monthSelectedListener: (CalendarMonth) -> Unit) {
    AndroidView(
        viewBlock = {
            CalendarView(it).apply {
                this.dayViewResource = R.layout.item_calendar_day_detail
                inDateStyle = InDateStyle.ALL_MONTHS
                maxRowCount = 6
                orientation = RecyclerView.HORIZONTAL
                outDateStyle = OutDateStyle.END_OF_ROW
                scrollMode = ScrollMode.PAGED
                daySize = CalendarView.sizeAutoWidth(
                    resources.getDimension(R.dimen.habit_detail_day_height).toInt()
                )
                setMonthPadding(16.px, 0, 16.px, 0)
                dayBinder = binder

                doOnLayout { view ->
                    view.updateLayoutParams { height = resources.getDimension(R.dimen.habit_detail_calendar_height).toInt() }
                }
            }
        }
    ) { view ->
        when (state.habitDetail) {
            is Incomplete -> Unit
            is Success -> {
                //binding.name.text = it.habitDetail()?.habit?.name
                view.layoutManager?.let {
                    view.notifyCalendarChanged()
                }
            }
            is Fail -> Unit
        }

        view.setup(state.startMonth, state.endMonth, state.days.first())
        view.scrollToMonth(state.selectedMonth)
        view.monthScrollListener = monthSelectedListener
    }
}