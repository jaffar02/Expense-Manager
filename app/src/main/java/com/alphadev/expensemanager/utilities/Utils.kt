package com.alphadev.expensemanager.utilities

import java.util.Date


class Utils {
    fun isCurrentMonth(comp_date: Date): Boolean {
        val date = Date()
        if (comp_date.year == date.year){
            if (comp_date.month == date.month) {
                return true
            }else {
                return false
            }
        }
        return false
    }
}