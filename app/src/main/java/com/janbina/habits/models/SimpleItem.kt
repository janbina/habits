package com.janbina.habits.models

import com.janbina.habits.R
import com.janbina.habits.databinding.ItemSimpleBinding
import com.janbina.habits.helpers.ViewBindingKotlinModel
import com.janbina.habits.models.firestore.HabitFirestore
import timber.log.Timber

data class SimpleItem(
    val habit: HabitDay,
    val onCompletedListener: (Boolean) -> Unit
) : ViewBindingKotlinModel<ItemSimpleBinding>(R.layout.item_simple) {

    override fun ItemSimpleBinding.bind() {
        title.text = habit.name
        checkBox.isChecked = habit.completed

        checkBox.setOnClickListener {
            Timber.e("CHECKED CHANGE ${habit.name}, ${checkBox.isChecked}")
            onCompletedListener(checkBox.isChecked)
        }

//        checkBox.setOnCheckedChangeListener { _, checked ->
//            Timber.e("CHECKED CHANGE ${habit.name}, $checked")
//            onCompletedListener(checked)
//        }
    }

}