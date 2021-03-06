package com.janbina.habits.ui.viewevent

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.janbina.habits.ui.base.FragmentArgs

abstract class NavigationEvent : ViewEvent() {

    abstract fun navigate(navController: NavController)

    companion object {
        operator fun invoke(@IdRes actionId: Int, arguments: Bundle = Bundle()) =
            NavDirectionsNavigationEvent(object : NavDirections {
                override fun getActionId() = actionId
                override fun getArguments() = arguments
            })

        operator fun invoke(@IdRes actionId: Int, args: FragmentArgs) =
            invoke(actionId, args.toBundle())

        fun back() = UpNavigationEvent()
    }
}

class NavDirectionsNavigationEvent(private val directions: NavDirections) : NavigationEvent() {
    override fun navigate(navController: NavController) {
        navController.navigate(directions)
    }
}

class UpNavigationEvent : NavigationEvent() {
    override fun navigate(navController: NavController) {
        navController.navigateUp()
    }
}