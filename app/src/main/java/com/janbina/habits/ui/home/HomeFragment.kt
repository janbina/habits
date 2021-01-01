package com.janbina.habits.ui.home

import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.janbina.habits.R
import com.janbina.habits.databinding.Example7DayBinding
import com.janbina.habits.databinding.FragmentHomeBinding
import com.janbina.habits.helpers.*
import com.janbina.habits.ui.LoginViewModel
import com.janbina.habits.ui.base.BaseFragment
import com.janbina.habits.util.onPageSelected
import com.janbina.habits.util.setMenuActions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var dateFormatters: DateFormatters
    private val viewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun setupRegistrations() {
        loginViewModel.liveData.observe(viewLifecycleOwner) {
            binding.loginSheet.isVisible = it.loggedIn.not()
            binding.loginInclude.loginProg.isVisible = it.inProgress
        }

        viewModel.liveData.observe(viewLifecycleOwner) {
            binding.toolbar.title = dateFormatters.formatRelative(it.selectedDate)
            updateDayStrip(it)

            binding.viewPager.setCurrentItem(
                it.selectedDate.toEpochDay().toInt(),
                binding.viewPager.currentItem != 0
            )
        }

        viewModel.handleNavigationEvents()

        loginViewModel.onEachEvent<LoginViewModel.SignInFinishedEvent> {
            when (it) {
                is LoginViewModel.SignInFinishedEvent.Success -> {
                    binding.viewPager.adapter = ViewPagerAdapter(this@HomeFragment)
                }
                is LoginViewModel.SignInFinishedEvent.Error -> {
                    Snackbar.make(binding.root, "Login failed, try again", Snackbar.LENGTH_SHORT).show()
                }
            }
        }.launchIn(lifecycleScope)

        loginViewModel.onEachEvent<LoginViewModel.LoggedOutEvent> {

        }.launchIn(lifecycleScope)
    }

    override fun setupView() = with(binding) {
        toolbar.setMenuActions(
            mapOf(
                R.id.menu_item_settings to viewModel::goToSettings,
                R.id.menu_item_create to viewModel::goToHabitCreation
            )
        )

        viewPager.adapter = ViewPagerAdapter(this@HomeFragment)

        loginInclude.loginButton.setOnClickListener {
            loginViewModel.loginWithGoogle()
        }

        viewPager.onPageSelected {
            if (it != 0) {
                dateSelected(it)
            }
        }

        repeat(7) {
            View.inflate(requireContext(), R.layout.example_7_day, daysLayout)
        }
    }

    private fun updateDayStrip(state: HomeState) {
        binding.daysLayout.children.forEachIndexed { index, view ->
            val b = Example7DayBinding.bind(view)
            val day = state.daysShown[index]
            b.dayNum.text = dateFormatters.dayNumFormatter.format(day)
            b.dayName.text = dateFormatters.shortDayNameFormatter.format(day)

            b.root.isSelected = day == state.selectedDate

//            if (day == state.selectedDate) {
//                b.dayNum.setTextColorRes(R.color.example_3_blue)
//                b.background.show()
//            } else {
//                b.dayNum.setTextColor(Color.WHITE)
//                b.background.hide()
//            }

            b.root.setOnClickListener {
                viewModel.dateChanged(day)
            }
        }
    }

    private fun dateSelected(date: Int) {
        viewModel.dateChanged(LocalDate.ofEpochDay(date.toLong()))
    }
}

private class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = Int.MAX_VALUE
    override fun createFragment(position: Int) = DayFragment.create(DayFragment.Args(position))
}