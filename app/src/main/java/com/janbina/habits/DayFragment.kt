package com.janbina.habits

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayFragment : Fragment(R.layout.fragment_day) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.text).text = argOr(ARG_DAY, "")
    }

    companion object {
        const val ARG_DAY = "ARG_DAY"

        fun create(day: String) = DayFragment().apply {
            arguments = bundleOf(
                ARG_DAY to day
            )
        }
    }
}

inline fun <reified T> Fragment.argOr(key: String, default: T): T {
    val arg = arguments?.get(key)
    if (arg is T) {
        return arg
    }
    return default
}