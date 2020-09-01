package com.janbina.habits.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRxView
import com.janbina.habits.ui.detail.HabitDetailScreen
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.launchIn

abstract class BaseComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent { content() }
        }
    }

    @Composable
    protected abstract fun content()

    protected fun BaseComposeViewModel<*>.handleNavigationEvents() {
        onEachEvent<NavigationEvent> {
            it.navigate(findNavController())
        }.launchIn(lifecycleScope)
    }
}