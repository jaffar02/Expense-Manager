package com.alphadev.expensemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.alphadev.expensemanager.database.IncomeDao
import com.alphadev.expensemanager.model.Income
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IncomesRepository(private val incomesDao: IncomeDao, private val firebaseDatabase: DatabaseReference) {
    val allIncomes: LiveData<List<Income>> = incomesDao.getAllIncomes()

    suspend fun insert(income: Income) {
        incomesDao.insertIncome(income)
        syncWithFirebase()
    }

    suspend fun delete(income: Income) {
        incomesDao.deleteIncome(income)
        syncWithFirebase()
    }

    suspend fun update(income: Income) {
        incomesDao.updateIncome(income)
        syncWithFirebase()
    }

    public fun syncWithFirebase() {
        allIncomes.value?.let { entities ->
            firebaseDatabase.setValue(entities)
        }
    }
}