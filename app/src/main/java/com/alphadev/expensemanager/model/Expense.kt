package com.alphadev.expensemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenseTable")
data class Expense (@PrimaryKey(autoGenerate = true) var id:Int=0, val date: Date,
                    val expenseDescription: String, val expenseAmount: Long, val pushId: String)