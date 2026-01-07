package com.example.expensetracker.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.model.BackupData
import com.example.expensetracker.model.database.PersonDbTable
import com.example.expensetracker.view.ExpenseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(private val repo: Repo) : ViewModel() {
    private val personList = repo.getAllPersons()
        .map { list ->
            // Sort persons by most recent activity (lastModified timestamp)
            // Most recently created or updated persons appear at the top
            list.sortedByDescending { person ->
                person.lastModified
            }.map { person ->
                // Sort expenses within each person by date (oldest first)
                // This ensures chronological order in the chat-like UI
                person.copy(
                    expenseList = person.expenseList.sortedBy { it.expenseDate }
                )
            }
        }
        .stateIn(
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
                    expenseList = state.value.expenseList.value,
                    lastModified = System.currentTimeMillis()
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
                    expenseList = state.value.expenseList.value,
                    lastModified = System.currentTimeMillis()
                )
            )
        }

        state.value.personId.value = null
        state.value.personName.value = ""
        state.value.amountReceivable.value = 0.0
        state.value.expenseList.value = emptyList()
    }

    fun deletePersons(persons: List<PersonDbTable>) {
        viewModelScope.launch {
            repo.deletePersons(persons)
        }
    }

    // ---- Import/Export Functions ----

    /**
     * Exports all data to a JSON file at the given Uri.
     * @param context Application context for content resolver.
     * @param uri The Uri to write the backup file to.
     * @param onComplete Callback with success/failure result.
     */
    fun exportData(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val backupData = repo.getBackupData()
                val jsonString = Json.encodeToString(backupData)

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    }
                }
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    /**
     * Imports data from a JSON file at the given Uri, replacing all existing data.
     * @param context Application context for content resolver.
     * @param uri The Uri of the backup file to read.
     * @param onComplete Callback with success/failure result.
     */
    fun importData(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                        reader.readText()
                    }
                }

                if (jsonString != null) {
                    val backupData = Json.decodeFromString<BackupData>(jsonString)
                    repo.restoreFromBackup(backupData)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}