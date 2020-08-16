package com.janbina.habits

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class HomeViewModel @ViewModelInject constructor(
    private val repo: SomeRepository
) : ViewModel() {

    fun getSth() = repo.getString()

}