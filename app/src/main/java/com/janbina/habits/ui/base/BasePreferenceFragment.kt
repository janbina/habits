package com.janbina.habits.ui.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceFragmentCompat
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewId

abstract class BasePreferenceFragment<T : ViewBinding>(
    private val bind: (View) -> T
) : PreferenceFragmentCompat(), MvRxView {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    private val mvrxViewIdProperty = MvRxViewId()
    override val mvrxViewId: String by mvrxViewIdProperty

    protected open fun setupView() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        mvrxViewIdProperty.restoreFrom(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this.viewLifecycleOwnerLiveData.value ?: this

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvrxViewIdProperty.saveTo(outState)
    }

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