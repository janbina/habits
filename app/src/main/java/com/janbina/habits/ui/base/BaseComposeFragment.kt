package com.janbina.habits.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.janbina.habits.ui.compose.HabitsTheme
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.launchIn

abstract class BaseComposeFragment : Fragment() {

    protected open fun setupView() {}
    protected open fun setupRegistrations() {}

    protected fun BaseReduxVM<*>.handleNavigationEvents() {
        onEachEvent<NavigationEvent> {
            it.navigate(findNavController())
        }.launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                HabitsTheme {
                    this@BaseComposeFragment.Content()
                }
            }
        }
    }

    @Composable
    protected abstract fun Content()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRegistrations()
    }
}