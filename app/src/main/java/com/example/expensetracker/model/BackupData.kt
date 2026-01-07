package com.example.expensetracker.model

import com.example.expensetracker.model.database.ExpenseModel
import kotlinx.serialization.Serializable

/**
 * Wrapper data class for backup JSON.
 * This structure is version-independent and allows for future expansion.
 *
 * @param version Schema version for future compatibility.
 * @param timestamp Unix timestamp (milliseconds) when the backup was created.
 * @param persons List of persons with their expenses.
 */
@Serializable
data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val persons: List<PersonBackup>
)

/**
 * Simplified person data for backup, without Room-specific annotations.
 * This decouples the backup format from the database entity.
 */
@Serializable
data class PersonBackup(
    val personName: String,
    val amountReceivable: Double,
    val expenses: List<ExpenseModel>
)
