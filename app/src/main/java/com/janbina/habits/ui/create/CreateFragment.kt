package com.janbina.habits.ui.create

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.ui.base.BaseComposeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class CreateFragment : BaseComposeFragment() {

    private val viewModel: CreateViewModel by viewModels()

    @Composable
    override fun content() = CreateScreen(
        navController = findNavController(),
        viewModel = viewModel,
    )

    override fun setupRegistrations() {
        viewModel.handleNavigationEvents()
    }

    @Parcelize
    data class Args(
        val id: String? = null
    ) : Parcelable
}

