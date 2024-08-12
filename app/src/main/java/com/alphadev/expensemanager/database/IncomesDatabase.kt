package com.alphadev.expensemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alphadev.expensemanager.model.Income
import com.alphadev.expensemanager.utilities.Converters

@Database(entities = arrayOf(Income::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class IncomesDatabase : RoomDatabase() {
    abstract fun getIncomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: IncomesDatabase?= null

        fun getIncomeDatabase(context: Context): IncomesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    IncomesDatabase::class.java,
                    "incomes_database")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}