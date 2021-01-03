package com.janbina.habits.ui.home

import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.janbina.habits.helpers.*
import com.janbina.habits.ui.LoginViewModel
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.compose.*
import com.janbina.habits.ui.detail.HabitEditation
import com.janbina.habits.util.onPageSelected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    @Inject
    lateinit var dateFormatters: DateFormatters
    private val viewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun setupRegistrations() {
//        loginViewModel.liveData.observe(viewLifecycleOwner) {
//            binding.loginSheet.isVisible = it.loggedIn.not()
//            binding.loginInclude.loginProg.isVisible = it.inProgress
//        }
//
//        viewModel.liveData.observe(viewLifecycleOwner) {
//            binding.toolbar.title = dateFormatters.formatRelative(it.selectedDate)
//            updateDayStrip(it)
//
//            binding.viewPager.setCurrentItem(
//                it.selectedDate.toEpochDay().toInt(),
//                binding.viewPager.currentItem != 0
//            )
//        }

        viewModel.handleNavigationEvents()

//        loginViewModel.onEachEvent<LoginViewModel.SignInFinishedEvent> {
//            when (it) {
//                is LoginViewModel.SignInFinishedEvent.Success -> {
//                    binding.viewPager.adapter = ViewPagerAdapter(this@HomeFragment)
//                }
//                is LoginViewModel.SignInFinishedEvent.Error -> {
//                    Snackbar.make(binding.root, "Login failed, try again", Snackbar.LENGTH_SHORT).show()
//                }
//            }
//        }.launchIn(lifecycleScope)

        loginViewModel.onEachEvent<LoginViewModel.LoggedOutEvent> {

        }.launchIn(lifecycleScope)
    }

    @Composable
    override fun content() {
        val _state by viewModel.liveData.observeAsState()
        val state = _state ?: return

        Column(modifier = Modifier.fillMaxSize()) {
            val (menuExpanded, setMenuExpanded) = remember { mutableStateOf(false) }
            TopAppBar(
                title = { Text(text = dateFormatters.formatRelative(state.selectedDate)) },
                actions = {
                    ToolbarDropdownMenu(
                        imageVector = Icons.Default.MoreVert,
                        expanded = menuExpanded,
                        setExpanded = setMenuExpanded,
                    ) {
                        SimpleDropdownMenuItem(
                            text = "Create new",
                            action = viewModel::createHabit,
                            setExpanded = setMenuExpanded,
                        )
                        DropdownMenuItem(
                            onClick = {
                                viewModel.setShowArchived(!state.showArchived)
                            }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    modifier = Modifier.weight(1F),
                                    text = "Show archived"
                                )
                                HorizontalSpacer(size = 16.dp)
                                Checkbox(
                                    checked = state.showArchived,
                                    onCheckedChange = viewModel::setShowArchived
                                )
                            }
                        }
                        SimpleDropdownMenuItem(
                            text = "Settings",
                            action = viewModel::goToSettings,
                            setExpanded = setMenuExpanded,
                        )
                    }
                }
            )
            DayStrip(state = state)
            Creation(viewModel, state)
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    viewBlock = { context ->
                        ViewPager2(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                            adapter = ViewPagerAdapter(this@HomeFragment)
                            onPageSelected { dateSelected(it) }
                        }
                    },
                    update = {
                        it.setCurrentItem(
                            state.selectedDate.toEpochDay().toInt(),
                            it.currentItem != 0
                        )
                    }
                )
                Box(modifier = Modifier.align(Alignment.BottomStart)) {
                    LoginSheet()
                }
            }
        }
    }

    @Composable
    fun ColumnScope.Creation(viewModel: HomeViewModel, state: HomeState) {
        AnimatedVisibility(visible = state.habitEditationVisible) {
            HabitEditation(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                state = state.habitEditationState,
                onStateChange = viewModel::updateCreation,
                onCancel = viewModel::cancelCreation,
                onSave = viewModel::saveCreation,
            )
        }
    }

    @Composable
    fun DayStrip(state: HomeState) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            state.daysShown.forEach {
                Day(
                    day = it,
                    isSelected = it == state.selectedDate,
                    { viewModel.dateChanged(it) }
                )
            }
        }
    }

    @Composable
    fun Day(
        day: LocalDate,
        isSelected: Boolean,
        onClick: () -> Unit,
    ) {
        val fontWeight = if (day == LocalDate.now()) FontWeight.SemiBold else FontWeight.Normal
        Column(
            modifier = Modifier.clickable(
                indication = rememberRipple(bounded = false, radius = 32.dp),
                onClick = onClick
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dateFormatters.shortDayNameFormatter.format(day).toUpperCase(Locale.US), fontWeight = fontWeight)
            Spacer(modifier = Modifier.preferredHeight(4.dp))
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.preferredSize(30.dp)
                        .background(color = Color.Transparent, shape = CircleShape)
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(),// + expandVertically(),
                    exit = fadeOut(),// + shrinkVertically(),
                ) {
                    Box(
                        modifier = Modifier.preferredSize(30.dp)
                            .background(color = Color.Blue, shape = CircleShape)
                    )
                }
                Text(text = dateFormatters.dayNumFormatter.format(day), fontWeight = fontWeight)
            }
        }
    }

    @Composable
    fun LoginSheet() {
        val _state by loginViewModel.liveData.observeAsState()
        val state = _state ?: return
        if (state.loggedIn.not()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                elevation = 8.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().invisible(state.inProgress.not())
                    )
                    VerticalSpacer(size = 8.dp)
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Login"
                    )
                    VerticalSpacer(size = 8.dp)
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Log in to create your own habits. They will be automatically backed up and available on all your devices."
                    )
                    VerticalSpacer(size = 8.dp)
                    OutlinedButton(
                        modifier = Modifier.align(Alignment.End).padding(horizontal = 16.dp),
                        onClick = { loginViewModel.loginWithGoogle() }
                    ) {
                        Text(text = "Login")
                    }
                    VerticalSpacer(size = 16.dp)
                }
            }
        }
    }

//    override fun setupView() = with(binding) {
//        toolbar.setMenuActions(
//            mapOf(
//                R.id.menu_item_settings to viewModel::goToSettings,
//                R.id.menu_item_create to viewModel::goToHabitCreation
//            )
//        )
//
//        viewPager.adapter = ViewPagerAdapter(this@HomeFragment)
//
//        loginInclude.loginButton.setOnClickListener {
//            loginViewModel.loginWithGoogle()
//        }
//
//        viewPager.onPageSelected {
//            if (it != 0) {
//                dateSelected(it)
//            }
//        }
//
//        repeat(7) {
//            View.inflate(requireContext(), R.layout.example_7_day, daysLayout)
//        }
//    }


    private fun dateSelected(date: Int) {
        viewModel.dateChanged(LocalDate.ofEpochDay(date.toLong()))
    }
}

private class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = Int.MAX_VALUE
    override fun createFragment(position: Int) = DayFragment.create(position)
}