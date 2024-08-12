package com.alphadev.expensemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.alphadev.expensemanager.database.IncomesDatabase
import com.alphadev.expensemanager.model.Income
import com.alphadev.expensemanager.repository.IncomesRepository
import com.alphadev.expensemanager.utilities.Utils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class IncomeViewModel(application: Application): AndroidViewModel(application) {
    val allIncomes : LiveData<List<Income>>
    val repository: IncomesRepository

    init {
        val dao = IncomesDatabase.getIncomeDatabase(application).getIncomeDao()
        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("Incomes")
        repository = IncomesRepository(dao, firebaseDatabase)
        allIncomes = repository.allIncomes
        allIncomes.observeForever {
            viewModelScope.launch {
                repository.syncWithFirebase()
            }
        }
    }

    fun deleteIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(income)
    }

    fun updateIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(income)
    }

    fun addIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(income)
    }

    fun getTotalIncome(): String {
        var totalIncome = 0L
        for (income in allIncomes.value!!) {
            if (Utils().isCurrentMonth(income.date)){
                totalIncome += income.incomeAmount
            }
        }
        return totalIncome.toString()
    }
}