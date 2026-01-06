package com.example.expensetracker.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "person_Db_Table")
@TypeConverters(Converters::class)
data class PersonDbTable(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var personName: String,
    var amountReceivable: Double,
    val expenseList: List<ExpenseModel>
)

@Serializable
data class ExpenseModel(
    var expenseName: String,
    var expenseAmount: Double,
    var expenseType: Boolean, // if true, receivable expense; if false, payable expense
    var expenseDate: Long = System.currentTimeMillis() // Date as timestamp in milliseconds
)

class Converters {
    @TypeConverter
    fun fromExpenseList(value: List<ExpenseModel>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toExpenseList(value: String): List<ExpenseModel> {
        return Json.decodeFromString(value)
    }
}