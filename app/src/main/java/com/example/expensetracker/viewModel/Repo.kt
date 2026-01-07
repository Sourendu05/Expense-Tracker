package com.example.expensetracker.viewModel

import com.example.expensetracker.model.BackupData
import com.example.expensetracker.model.PersonBackup
import com.example.expensetracker.model.database.PersonDatabase
import com.example.expensetracker.model.database.PersonDbTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Repo(val database: PersonDatabase) {
    suspend fun upsertPerson(person: PersonDbTable) {
        database.personDbDao().upsertPerson(person)
    }

    suspend fun deletePerson(person: PersonDbTable) {
        database.personDbDao().deletePerson(person)
    }

    suspend fun deletePersons(persons: List<PersonDbTable>) {
        database.personDbDao().deletePersons(persons)
    }

    fun getAllPersons(): Flow<List<PersonDbTable>> {
        return database.personDbDao().getAllPersons()
    }

    // ---- Import/Export Methods ----

    /**
     * Creates a BackupData object from the current database state.
     */
    suspend fun getBackupData(): BackupData {
        val persons = database.personDbDao().getAllPersons().first()
        return BackupData(
            persons = persons.map { person ->
                PersonBackup(
                    personName = person.personName,
                    amountReceivable = person.amountReceivable,
                    expenses = person.expenseList
                )
            }
        )
    }

    /**
     * Restores data from a BackupData object, replacing all existing data.
     */
    suspend fun restoreFromBackup(backupData: BackupData) {
        // Clear existing data
        database.personDbDao().deleteAll()

        // Insert new data from backup
        val persons = backupData.persons.map { personBackup ->
            PersonDbTable(
                id = null, // Auto-generate new IDs
                personName = personBackup.personName,
                amountReceivable = personBackup.amountReceivable,
                expenseList = personBackup.expenses,
                lastModified = System.currentTimeMillis() // Set to current time on restore
            )
        }
        database.personDbDao().insertAll(persons)
    }
}