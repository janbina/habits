package com.janbina.habits.ui.detail

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Incomplete
import com.airbnb.mvrx.Success
import com.janbina.habits.R
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.helpers.px
import com.janbina.habits.ui.compose.*
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder

@Composable
fun HabitDetailScreen(
    dateFormatters: DateFormatters,
    navController: NavController,
    binder: DayBinder<*>
) {
    val viewModel: HabitDetailViewModel = viewModel()
    val viewState by viewModel.liveData.observeAsState()

    var showDeleteDialog by savedInstanceState { false }

    Providers(
        DateFormatterAmbient provides dateFormatters
    ) {
        HabitsTheme {
            viewState?.let {
                Column {
                    HabitsAppBar(
                        onNavIconPressed = navController::navigateUp,
                        actions = {
                            ToolbarButton(asset = Icons.Filled.Edit, onClick = viewModel::edit)
                            ToolbarButton(
                                asset = Icons.Filled.Delete,
                                onClick = { showDeleteDialog = true }
                            )
                        }
                    )
                    HabitHeader(it)
                    DayLegend(it)
                    Calendar(it, binder, viewModel::monthSelected)
                }

                if (showDeleteDialog) {
                    DeleteDialog(
                        state = it,
                        viewModel = viewModel,
                        hide = { showDeleteDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(state: HabitDetailState, viewModel: HabitDetailViewModel, hide: () -> Unit) {
    AlertDialog(
        onDismissRequest = hide,
        buttons = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth().padding(end = 8.dp, bottom = 8.dp)
            ) {
                TextButton(onClick = hide, contentColor = MaterialTheme.colors.onSurface) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = viewModel::delete, contentColor = DeleteRed) {
                    Text(text = "Delete")
                }
            }
        },
        title = {
            Text(text = "Delete ${state.habitDetail()?.habit?.name}")
        },
        text = {
            Text("Are you sure you want to delete this habit? You will lose all the history and it cannot be undone.")
        },
    )
}

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
                ToolbarButton(asset = Icons.Default.ArrowBack, onClick = onNavIconPressed)
            }
        )
        Divider()
    }
}

@Composable
fun HabitHeader(state: HabitDetailState) {
    val habit = state.habitDetail()
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(text = habit?.habit?.name ?: "")
        Text(text = DateFormatterAmbient.current.formatMonthNameOptionalYear(state.selectedMonth))
    }
}

@Composable
fun DayLegend(state: HabitDetailState) {
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
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.W300
                )
            )
        }
    }
}

@Composable
fun Calendar(
    state: HabitDetailState,
    binder: DayBinder<*>,
    monthSelectedListener: (CalendarMonth) -> Unit
) {
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