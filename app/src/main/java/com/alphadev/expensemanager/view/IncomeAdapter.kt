package com.alphadev.expensemanager.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.alphadev.expensemanager.R
import com.alphadev.expensemanager.databinding.IncomeAdapterFaceBinding
import com.alphadev.expensemanager.model.Income
import java.text.SimpleDateFormat

class IncomeAdapter(val context: Context, val delInterface: IncomeItemDeleteInterface,
    val editInterface: IncomeItemEditInterface) : RecyclerView.Adapter<IncomeAdapter.ViewHolder>(){

    private val allIncomesList: ArrayList<Income> = ArrayList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: IncomeAdapterFaceBinding = IncomeAdapterFaceBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.income_adapter_face, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = allIncomesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat)
        holder.binding.dateIncome.text = dateFormat.format(allIncomesList[position].date)
        holder.binding.incomeAmount.text = allIncomesList[position].incomeAmount.toString()
        holder.binding.descriptionIncome.text = allIncomesList[position].incomeDescription

        holder.binding.deleteBtn.setOnClickListener {
            delInterface.onIncomeDeleteIconClick(allIncomesList[position])
        }

        holder.binding.editBtn.setOnClickListener {
            editInterface.onIncomeEditIconClick(allIncomesList[position])
        }
    }

    fun updateList(newList: List<Income>) {
        allIncomesList.clear()
        allIncomesList.addAll(newList)
        notifyDataSetChanged()
    }

    interface IncomeItemDeleteInterface {
        fun onIncomeDeleteIconClick(income: Income)
    }

    interface IncomeItemEditInterface {
        fun onIncomeEditIconClick(income: Income)
    }
}