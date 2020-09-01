package com.janbina.habits.ui.detail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
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
import com.google.firebase.auth.ktx.actionCodeSettings
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
fun HabitsAppBar(
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        TopAppBar(
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            actions = actions,
            title = title,
            navigationIcon = {
                IconButton(
                    icon = { Icon(asset = Icons.Default.ArrowBack) },
                    onClick = onNavIconPressed
                )
            }
        )
        Divider()
    }
}

@Composable
fun HabitHeader(state: HabitDetailStateCompose) {
    val habit = state.habitDetail()
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(text = habit?.habit?.name ?: "")
        Text(text = DateFormatterAmbient.current.formatMonthNameOptionalYear(state.selectedMonth))
    }
}

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