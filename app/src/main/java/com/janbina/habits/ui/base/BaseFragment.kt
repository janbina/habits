package com.janbina.habits.ui.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.*
import com.janbina.habits.ui.viewevent.NavigationEvent
import kotlinx.coroutines.flow.*

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment(), MavericksView {

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
    fun toBundle() = bundleOf(Mavericks.KEY_ARG to this)
}