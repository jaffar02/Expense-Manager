package com.alphadev.expensemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.alphadev.expensemanager.model.Expense
import com.alphadev.expensemanager.model.Income
import com.alphadev.expensemanager.utilities.Converters

@Database(entities = arrayOf(Expense::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ExpensesDatabase : RoomDatabase() {
    abstract fun getExpenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: ExpensesDatabase?= null

        fun getExpenseDatabase(context: Context): ExpensesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    ExpensesDatabase::class.java,
                    "expenses_database")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}