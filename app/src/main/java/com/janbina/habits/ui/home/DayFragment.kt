package com.janbina.habits.ui.home

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.janbina.habits.di.viewModelProviderFactoryOf
import com.janbina.habits.models.HabitDay
import com.janbina.habits.ui.base.BaseComposeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class DayFragment : BaseComposeFragment() {

    @Inject
    internal lateinit var vmFactory: DayViewModel.Factory

    private val viewModel: DayViewModel by viewModels {
        viewModelProviderFactoryOf {
            vmFactory.create(requireArguments().getInt(KEY_DAY))
        }
    }

    override fun setupRegistrations() {
        viewModel.handleNavigationEvents()
    }

    @Composable
    override fun content() {
        val state by viewModel.liveData.observeAsState()
        val habits = state?.habits?.invoke() ?: return
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(habits) {
                HabitItem(habit = it)
            }
        }
    }

    @Composable
    fun HabitItem(habit: HabitDay) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    onClick = { viewModel.openHabit(habit) }
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1F),
                text = habit.name
            )
            Checkbox(
                checked = habit.completed,
                onCheckedChange = { viewModel.markHabitAsCompleted(habit, it) }
            )
        }
    }

    companion object {
        private const val KEY_DAY = "KEY_DAY"
        fun create(day: Int) = DayFragment().apply { arguments = bundleOf(KEY_DAY to day) }
    }
}
