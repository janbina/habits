package com.janbina.habits.di.helpers

import androidx.fragment.app.FragmentActivity
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.janbina.habits.ui.base.BaseViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

abstract class DaggerVmFactory<VM : BaseViewModel<S>, S : MavericksState>(
    private val viewModelClass: Class<out BaseViewModel<S>>
) : MavericksViewModelFactory<VM, S> {

    override fun create(viewModelContext: ViewModelContext, state: S): VM? {
        return createViewModel(viewModelContext.activity, state)
    }

    private fun <VM : BaseViewModel<S>, S : MavericksState> createViewModel(fragmentActivity: FragmentActivity, state: S): VM {
        val viewModelFactoryMap = EntryPoints.get(
            fragmentActivity.applicationContext, DaggerMvrxViewModelFactoryEntryPoint::class.java
        ).viewModelFactories
        val viewModelFactory = viewModelFactoryMap[viewModelClass]
        @Suppress("UNCHECKED_CAST")
        val castedViewModelFactory = viewModelFactory as? AssistedViewModelFactory<VM, S>
        val viewModel = castedViewModelFactory?.create(state)
        return viewModel as VM
    }
}

@EntryPoint
@InstallIn(ApplicationComponent::class)
interface DaggerMvrxViewModelFactoryEntryPoint {
    val viewModelFactories: Map<Class<out BaseViewModel<*>>, AssistedViewModelFactory<*, *>>
}