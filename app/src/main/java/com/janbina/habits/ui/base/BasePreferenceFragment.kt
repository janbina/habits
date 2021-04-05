package com.janbina.habits.ui.base

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.viewbinding.ViewBinding

abstract class BasePreferenceFragment<T : ViewBinding>(
    private val bind: (View) -> T
) : PreferenceFragmentCompat() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected open fun setupView() {}

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