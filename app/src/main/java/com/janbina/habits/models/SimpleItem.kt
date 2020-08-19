package com.janbina.habits.models

import com.janbina.habits.R
import com.janbina.habits.databinding.ItemSimpleBinding
import com.janbina.habits.helpers.ViewBindingKotlinModel

data class SimpleItem(
    val habit: HabitDay,
    val onCompletedListener: (Boolean) -> Unit,
    val onClickListener: () -> Unit
) : ViewBindingKotlinModel<ItemSimpleBinding>(R.layout.item_simple) {

    override fun ItemSimpleBinding.bind() {
        title.text = habit.name
        checkBox.isChecked = habit.completed

        checkBox.setOnClickListener {
            onCompletedListener(checkBox.isChecked)
        }

        wrapper.setOnClickListener {
            onClickListener()
        }
    }

}