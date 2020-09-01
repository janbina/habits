package com.janbina.habits.ui.base

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRxView
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.launchIn

abstract class BaseComposeFragment : BaseMvRxFragment(), MvRxView {

    protected fun BaseComposeViewModel<*>.handleNavigationEvents() {
        onEachEvent<NavigationEvent> {
            it.navigate(findNavController())
        }.launchIn(lifecycleScope)
    }
}