package com.janbina.habits.ui.home

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.janbina.habits.ui.compose.VerticalSpacer
import com.janbina.habits.ui.compose.invisible
import com.janbina.habits.util.onPageSelected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDate
import javax.inject.Inject

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
            TopAppBar {
                Text(text = dateFormatters.formatRelative(state.selectedDate))
            }
            DayStrip(state = state)
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
        val color = if (isSelected) Color.Blue else Color.Transparent
        Column(
            modifier = Modifier.clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dateFormatters.shortDayNameFormatter.format(day))
            Spacer(modifier = Modifier.preferredHeight(4.dp))
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.preferredSize(30.dp)
                        .background(color = color, shape = CircleShape)
                )
                Text(text = dateFormatters.dayNumFormatter.format(day))
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
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().invisible(state.inProgress.not()))
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