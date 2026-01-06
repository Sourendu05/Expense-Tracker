package com.example.expensetracker.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDbDao {
    @Upsert
    suspend fun upsertPerson(person: PersonDbTable)

    @Delete
    suspend fun deletePerson(person: PersonDbTable)

    @Query("SELECT * FROM person_Db_Table")
    fun getAllPersons(): Flow<List<PersonDbTable>>
}