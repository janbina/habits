package com.janbina.habits.di.helpers

import androidx.fragment.app.FragmentActivity
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.janbina.habits.ui.base.BaseViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

abstract class DaggerVmFactory<VM : BaseViewModel<S>, S : MvRxState>(
    private val viewModelClass: Class<out BaseViewModel<S>>
) : MvRxViewModelFactory<VM, S> {

    override fun create(viewModelContext: ViewModelContext, state: S): VM? {
        return createViewModel(viewModelContext.activity, state)
    }

    private fun <VM : BaseViewModel<S>, S : MvRxState> createViewModel(fragmentActivity: FragmentActivity, state: S): VM {
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