package com.janbina.habits.ui.create

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import com.janbina.habits.ui.detail.HabitDetailScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class CreateFragmentCompose : BaseComposeFragment() {

    private val viewModel: CreateViewModelCompose by viewModels()

    @Composable
    override fun content() = CreateScreen(
        navController = findNavController()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handleNavigationEvents()
    }

    @Parcelize
    data class Args(
        val id: String? = null
    ) : FragmentArgs()
}

