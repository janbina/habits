package com.janbina.habits.data

import android.os.Build

object ApiLevels {

    val supportsSystemNightMode get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

}