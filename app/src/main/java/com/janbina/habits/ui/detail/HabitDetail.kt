package com.janbina.habits.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.janbina.habits.R
import com.janbina.habits.helpers.DateFormatters
import com.janbina.habits.helpers.px
import com.janbina.habits.models.Fail
import com.janbina.habits.models.Incomplete
import com.janbina.habits.models.Success
import com.janbina.habits.ui.compose.*
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.model.ScrollMode
import com.kizitonwose.calendarview.ui.DayBinder
import java.time.LocalDate

@Composable
fun HabitDetailScreen(
    dateFormatters: DateFormatters,
    navController: NavController,
    binder: DayBinder<*>,
    viewModel: HabitDetailViewModel,
) {
    val viewState by viewModel.liveData.observeAsState(
        initial = HabitDetailState(
            "xxx",
            LocalDate.now()
        )
    )

    var showDeleteDialog by savedInstanceState { false }

    Providers(
        AmbientDateFormatter provides dateFormatters
    ) {
        HabitsTheme {
            ScrollableColumn {
                val archiveAction = if (viewState.habitDetail()?.habit?.archived == true) "Restore" else "Archive"
                val (menuExpanded, setMenuExpanded) = remember { mutableStateOf(false) }
                HabitsAppBar(
                    onNavIconPressed = navController::navigateUp,
                    actions = {
                        ToolbarDropdownMenu(
                            imageVector = Icons.Default.MoreVert,
                            expanded = menuExpanded,
                            setExpanded = setMenuExpanded,
                            items = listOf(
                                "Edit" to viewModel::edit,
                                archiveAction to viewModel::toggleArchived,
                                "Delete" to { showDeleteDialog = true }
                            )
                        )
                    }
                )
                Stats(viewState)
                HabitHeader(viewModel, viewState)
                DayLegend(viewState)
                Calendar(viewState, binder, viewModel::monthSelected)
                ArchiveInfo(viewModel, viewState)
            }

            if (showDeleteDialog) {
                DeleteDialog(
                    state = viewState,
                    viewModel = viewModel,
                    hide = { showDeleteDialog = false }
                )
            }
        }
    }
}

@Composable
fun ArchiveInfo(viewModel: HabitDetailViewModel, viewState: HabitDetailState) {
    val habit = viewState.habitDetail()?.habit ?: return
    if (!habit.archived) return
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = "Archived")
        Text(text = "This habit is archived. It will be only displayed on days when you completed it. You can access archive from home screen menu and unarchive any habit from its detail.")
        OutlinedButton(onClick = viewModel::toggleArchived) {
            Text(text = "Unarchive")
        }
    }
}

@Composable
fun Stats(viewState: HabitDetailState) {
    val habitDetail = viewState.habitDetail() ?: return
    Column {
        Text(text = "Year to date: ${habitDetail.yearToDateCount()}")
        Text(text = "This year: ${habitDetail.thisYearCount()}")
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
                TextButton(onClick = hide){//, contentColor = MaterialTheme.colors.onSurface) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = viewModel::delete){//, contentColor = DeleteRed) {
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
fun HabitHeader(
    viewModel: HabitDetailViewModel,
    state: HabitDetailState
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Crossfade(current = state.habitEditationVisible) {
            if (it) {
                HabitEditation(
                    state = state.habitEditationState,
                    onStateChange = viewModel::updateEdit,
                    onCancel = viewModel::cancelEdit,
                    onSave = viewModel::saveEdit,
                )
            } else {
                Text(text = state.habitDetail()?.habit?.name ?: "")
            }
        }
        Text(text = AmbientDateFormatter.current.formatMonthNameOptionalYear(state.selectedMonth))
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
                text = AmbientDateFormatter.current.shortDayNameFormatter.format(it).toUpperCase(),
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
            is Success<*> -> {
                view.layoutManager?.let {
                    view.notifyCalendarChanged()
                }
            }
            is Fail<*> -> Unit
        }

        view.setup(state.startMonth, state.endMonth, state.days.first())
        view.scrollToMonth(state.selectedMonth)
        view.monthScrollListener = monthSelectedListener
    }
}