package com.example.expensetracker.model.di

import android.app.Application
import androidx.room.Room
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
    @Provides
    @Singleton
    fun providePersonDatabase(application: Application): PersonDatabase {
        return Room.databaseBuilder(
            application,
            PersonDatabase::class.java,
            "person_Db_Table"
        ).build()
    }

    @Provides
    @Singleton
    fun providePersonRepository(database: PersonDatabase): Repo {
        return Repo(database)
    }
}