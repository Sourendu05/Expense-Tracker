package com.example.expensetracker.model.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.model.database.PersonDatabase
import com.example.expensetracker.viewModel.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object diModule {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add lastModified column with current timestamp as default
            db.execSQL("ALTER TABLE person_Db_Table ADD COLUMN lastModified INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        }
    }

    @Provides
    @Singleton
    fun providePersonDatabase(application: Application): PersonDatabase {
        return Room.databaseBuilder(
            application,
            PersonDatabase::class.java,
            "person_Db_Table"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun providePersonRepository(database: PersonDatabase): Repo {
        return Repo(database)
    }
}