package com.janbina.habits

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SomeRepository @Inject constructor() {

    fun getString() = "Some string"

}