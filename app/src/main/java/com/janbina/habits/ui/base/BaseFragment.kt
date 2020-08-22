package com.janbina.habits.ui.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxView

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseMvRxFragment(), MvRxView {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

abstract class FragmentArgs : Parcelable {
    fun toBundle() = bundleOf(MvRx.KEY_ARG to this)
}