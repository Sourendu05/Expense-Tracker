package com.example.expensetracker.viewModel

import com.example.expensetracker.model.database.PersonDatabase
import com.example.expensetracker.model.database.PersonDbTable
import kotlinx.coroutines.flow.Flow

class Repo(val database: PersonDatabase) {
    suspend fun upsertPerson(person: PersonDbTable) {
        database.personDbDao().upsertPerson(person)
    }

    suspend fun deletePerson(person: PersonDbTable) {
        database.personDbDao().deletePerson(person)
    }

    fun getAllPersons(): Flow<List<PersonDbTable>> {
        return database.personDbDao().getAllPersons()
    }
}