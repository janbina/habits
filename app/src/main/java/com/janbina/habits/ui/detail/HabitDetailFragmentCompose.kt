package com.janbina.habits.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.airbnb.mvrx.BaseMvRxFragment
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class HabitDetailFragmentCompose : BaseComposeFragment() {

    override fun invalidate() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ExampleText()
                }
            }
        }
    }

    @Composable
    fun ExampleText() {
        Text("Hello Compose")
    }

    @Parcelize
    data class Args(
        val id: String,
        val day: Int
    ) : FragmentArgs()

}