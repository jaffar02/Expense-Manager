package com.alphadev.expensemanager.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alphadev.expensemanager.R
import com.alphadev.expensemanager.databinding.ExpenseAdapterFaceBinding
import com.alphadev.expensemanager.databinding.IncomeAdapterFaceBinding
import com.alphadev.expensemanager.model.Expense
import com.alphadev.expensemanager.model.Income
import java.text.SimpleDateFormat

class ExpenseAdapter(val context: Context, val delInterface: ExpenseItemDeleteInterface,
                     val editInterface: ExpenseItemEditInterface) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>(){

    private val allExpensesList: ArrayList<Expense> = ArrayList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ExpenseAdapterFaceBinding = ExpenseAdapterFaceBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.expense_adapter_face, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = allExpensesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat)
        holder.binding.dateExpense.text = dateFormat.format(allExpensesList[position].date)
        holder.binding.expenseAmount.text = allExpensesList[position].expenseAmount.toString()
        holder.binding.descriptionExpense.text = allExpensesList[position].expenseDescription

        holder.binding.deleteBtn.setOnClickListener {
            delInterface.onExpenseDeleteIconClick(allExpensesList[position])
        }

        holder.binding.editBtn.setOnClickListener {
            editInterface.onExpenseEditIconClick(allExpensesList[position])
        }
    }

    fun updateList(newList: List<Expense>) {
        allExpensesList.clear()
        allExpensesList.addAll(newList)
        notifyDataSetChanged()
    }

    interface ExpenseItemDeleteInterface {
        fun onExpenseDeleteIconClick(expense: Expense)
    }

    interface ExpenseItemEditInterface {
        fun onExpenseEditIconClick(expense: Expense)
    }
}