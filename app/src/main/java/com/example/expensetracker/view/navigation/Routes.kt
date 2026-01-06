package com.example.expensetracker.view.navigation

import kotlinx.serialization.Serializable

@Serializable
object AllPersonsScreenUI

@Serializable
data class PersonExpenseScreenUI(
    var personId: Int? = null
)