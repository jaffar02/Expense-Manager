package com.alphadev.expensemanager.repository

import androidx.lifecycle.LiveData
import com.alphadev.expensemanager.database.ExpenseDao
import com.alphadev.expensemanager.database.IncomeDao
import com.alphadev.expensemanager.model.Expense
import com.alphadev.expensemanager.model.Income
import com.google.firebase.database.DatabaseReference

class ExpensesRepository(private val expenseDao: ExpenseDao, private val firebaseDatabase: DatabaseReference) {
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
        syncWithFirebase()
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
        syncWithFirebase()
    }

    suspend fun update(expense: Expense) {
        expenseDao.updateExpense(expense)
        syncWithFirebase()
    }

    public fun syncWithFirebase() {
        allExpenses.value?.let { entities ->
            firebaseDatabase.setValue(entities)
        }
    }
}