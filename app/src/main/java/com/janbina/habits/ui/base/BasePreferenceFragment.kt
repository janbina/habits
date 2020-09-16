package com.janbina.habits.ui.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceFragmentCompat
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.MavericksView

abstract class BasePreferenceFragment<T : ViewBinding>(
    private val bind: (View) -> T
) : PreferenceFragmentCompat(), MavericksView {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected open fun setupView() {}

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this.viewLifecycleOwnerLiveData.value ?: this

    override fun onStart() {
        super.onStart()
        postInvalidate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = bind(view)
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}