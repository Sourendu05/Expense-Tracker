package com.example.expensetracker.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.expensetracker.model.database.ExpenseModel
import com.example.expensetracker.model.database.PersonDbTable

data class ExpenseState(
    val personList: List<PersonDbTable> = emptyList(),

    val personId: MutableState<Int?> = mutableStateOf(null),
    val personName: MutableState<String> = mutableStateOf(""),
    val amountReceivable: MutableState<Double> = mutableStateOf(0.0),
    val expenseList: MutableState<List<ExpenseModel>> = mutableStateOf(emptyList()),
)