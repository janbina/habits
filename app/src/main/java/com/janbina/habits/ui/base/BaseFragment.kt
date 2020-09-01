package com.janbina.habits.ui.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxView
import com.janbina.habits.ui.detail.HabitDetailFragmentCompose
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.*

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseMvRxFragment(), MvRxView {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected open fun setupView() {}
    protected open fun setupRegistrations() {}

    protected fun BaseViewModel<*>.handleNavigationEvents() {
        onEachEvent<NavigationEvent> {
            it.navigate(findNavController())
        }.launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRegistrations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

abstract class FragmentArgs : Parcelable {
    fun toBundle() = bundleOf(MvRx.KEY_ARG to this)
}

inline fun <reified T: FragmentArgs> SavedStateHandle.getArgs(): T {
    return get<T>(MvRx.KEY_ARG)!!
}