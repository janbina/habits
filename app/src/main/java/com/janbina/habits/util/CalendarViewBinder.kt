package com.janbina.habits.util

import android.view.View
import androidx.viewbinding.ViewBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer

class BindingDayBinder<T : ViewBinding>(
    private val binder: (View) -> T,
    private val onBind: T.(day: CalendarDay) -> Unit
) : DayBinder<BindingContainer<T>> {
    override fun create(view: View) = BindingContainer(view, binder)

    override fun bind(container: BindingContainer<T>, day: CalendarDay) {
        return onBind(container.binding, day)
    }

}

class BindingContainer<T : ViewBinding>(view: View, bind: (View) -> T) : ViewContainer(view) {
    val binding = bind(view)
}