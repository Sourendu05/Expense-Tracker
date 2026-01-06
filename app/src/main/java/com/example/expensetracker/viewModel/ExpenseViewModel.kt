package com.example.expensetracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.model.database.PersonDbTable
import com.example.expensetracker.view.ExpenseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(private val repo: Repo) : ViewModel() {
    private val personList = repo.getAllPersons().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )
    private val _state = MutableStateFlow(ExpenseState())

    val state = combine(_state, personList) { _state, personList ->
        _state.copy(personList = personList)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ExpenseState()
    )

    fun upsertPerson() {
        viewModelScope.launch {
            repo.upsertPerson(
                PersonDbTable(
                    id = state.value.personId.value,
                    personName = state.value.personName.value,
                    amountReceivable = state.value.amountReceivable.value,
                    expenseList = state.value.expenseList.value
                )
            )
        }
    }

    fun deletePerson() {
        viewModelScope.launch {
            repo.deletePerson(
                PersonDbTable(
                    id = state.value.personId.value,
                    personName = state.value.personName.value,
                    amountReceivable = state.value.amountReceivable.value,
                    expenseList = state.value.expenseList.value
                )
            )
        }

        state.value.personId.value = null
        state.value.personName.value = ""
        state.value.amountReceivable.value = 0.0
        state.value.expenseList.value = emptyList()
    }
}