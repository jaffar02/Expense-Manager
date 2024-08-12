package com.alphadev.expensemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.alphadev.expensemanager.database.ExpensesDatabase
import com.alphadev.expensemanager.database.IncomesDatabase
import com.alphadev.expensemanager.model.Expense
import com.alphadev.expensemanager.model.Income
import com.alphadev.expensemanager.repository.ExpensesRepository
import com.alphadev.expensemanager.repository.IncomesRepository
import com.alphadev.expensemanager.utilities.Utils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application): AndroidViewModel(application) {
    val allExpenses : LiveData<List<Expense>>
    val repository: ExpensesRepository

    init {
        val dao = ExpensesDatabase.getExpenseDatabase(application).getExpenseDao()
        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("Expenses")
        repository = ExpensesRepository(dao, firebaseDatabase)
        allExpenses = repository.allExpenses
        allExpenses.observeForever {
            viewModelScope.launch {
                repository.syncWithFirebase()
            }
        }
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(expense)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(expense)
    }

    fun getTotalExpense(): String {
        var totalExpense = 0L
        for (income in allExpenses.value!!) {
            if (Utils().isCurrentMonth(income.date)){
                totalExpense += income.expenseAmount
            }
        }
        return totalExpense.toString()
    }
}