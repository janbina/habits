package com.janbina.habits.ui.detail

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Incomplete
import com.airbnb.mvrx.Success
import com.janbina.habits.R
import com.janbina.habits.helpers.px
import com.janbina.habits.theme.Rubik
import com.janbina.habits.ui.compose.DateFormatterAmbient
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder

@Composable
fun DayLegend(state: HabitDetailStateCompose) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        state.days.forEach { 
            Text(
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center,
                text = DateFormatterAmbient.current.shortDayNameFormatter.format(it).toUpperCase(),
                style = MaterialTheme.typography.body1.copy(
                    fontSize = 10.sp,
                    fontFamily = Rubik,
                    fontWeight = FontWeight.W300
                )
            )
        }
    }
}

@Composable
fun Calendar(state: HabitDetailStateCompose, binder: DayBinder<*>, monthSelectedListener: (CalendarMonth) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
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