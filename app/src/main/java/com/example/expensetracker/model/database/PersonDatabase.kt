package com.example.expensetracker.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PersonDbTable::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PersonDatabase: RoomDatabase() {
    abstract fun personDbDao(): PersonDbDao
}