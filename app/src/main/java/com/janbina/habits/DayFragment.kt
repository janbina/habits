package com.janbina.habits

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DayFragment : Fragment(R.layout.fragment_day) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysSinceEpoch = argOr(ARG_DAY, 0)

        view.findViewById<TextView>(R.id.text).text = LocalDate.ofEpochDay(daysSinceEpoch.toLong()).format(
            DateTimeFormatter.ISO_LOCAL_DATE)
    }

    companion object {
        const val ARG_DAY = "ARG_DAY"

        fun create(daysSinceEpoch: Int) = DayFragment().apply {
            arguments = bundleOf(
                ARG_DAY to daysSinceEpoch
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