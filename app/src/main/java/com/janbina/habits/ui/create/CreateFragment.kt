package com.janbina.habits.ui.create

import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.janbina.habits.ui.base.BaseComposeFragment
import com.janbina.habits.ui.base.FragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class CreateFragment : BaseComposeFragment() {

    private val viewModel: CreateViewModel by fragmentViewModel()

    @Composable
    override fun content() = CreateScreen(
        navController = findNavController()
    )

    override fun setupRegistrations() {
        viewModel.handleNavigationEvents()
    }

    override fun invalidate() {}

    @Parcelize
    data class Args(
        val id: String? = null
    ) : FragmentArgs()
}

