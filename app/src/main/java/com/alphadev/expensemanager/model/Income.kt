package com.alphadev.expensemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "incomeTable")
data class Income (@PrimaryKey(autoGenerate = true) var id:Int=0, val date: Date, val incomeDescription: String,
                   val incomeAmount: Long, val pushId: String)