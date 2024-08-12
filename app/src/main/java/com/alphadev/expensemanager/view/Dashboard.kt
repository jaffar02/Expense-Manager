package com.alphadev.expensemanager.view

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alphadev.expensemanager.R
import com.alphadev.expensemanager.databinding.ActivityDashboardBinding
import com.alphadev.expensemanager.model.Expense
import com.alphadev.expensemanager.model.Income
import com.alphadev.expensemanager.utilities.AlertDialogTemplate
import com.alphadev.expensemanager.viewmodel.ExpenseViewModel
import com.alphadev.expensemanager.viewmodel.IncomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Dashboard : AppCompatActivity(), IncomeAdapter.IncomeItemEditInterface,
        IncomeAdapter.IncomeItemDeleteInterface, ExpenseAdapter.ExpenseItemDeleteInterface,
        ExpenseAdapter.ExpenseItemEditInterface{

    private val TAG: String = "Dashboard"
    private lateinit var incomesAdapter: IncomeAdapter
    private lateinit var expensesAdapter: ExpenseAdapter
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var expenseViewModel: ExpenseViewModel
    private var totalIncome = 0L
    private var totalExpense = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        incomesAdapter = IncomeAdapter(this@Dashboard, this, this)
        expensesAdapter = ExpenseAdapter(this@Dashboard, this, this)
        binding.incomeAdapter.adapter = incomesAdapter
        binding.expensesAdapter.adapter = expensesAdapter
        incomeViewModel = ViewModelProvider(this@Dashboard, ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)).get(IncomeViewModel::class.java)
        expenseViewModel = ViewModelProvider(this@Dashboard, ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)).get(ExpenseViewModel::class.java)

        incomeViewModel.allIncomes.observe(this@Dashboard, { list ->
            list?.let {
                incomesAdapter.updateList(list)
                binding.totalIncome.text = incomeViewModel.getTotalIncome()
                totalIncome = incomeViewModel.getTotalIncome().toLong()
                binding.remainingIncome.text = (totalIncome - totalExpense).toString()
            }
        })
        expenseViewModel.allExpenses.observe(this@Dashboard, { list ->
            list?.let {
                expensesAdapter.updateList(list)
                binding.totalExpense.text = expenseViewModel.getTotalExpense()
                totalExpense = expenseViewModel.getTotalExpense().toLong()
                binding.remainingIncome.text = (totalIncome - totalExpense).toString()
            }
        })

        binding.addIncomeButton.setOnClickListener {
            processIncomeAlertDialog("insert")
        }

        binding.addExpenseButton.setOnClickListener {
            processExpenseAlertDialog("insert")
        }
    }

    override fun onStart() {
        super.onStart()
        window.statusBarColor = resources.getColor(R.color.actionbar)
        supportActionBar?.title = "Dashboard"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.actionbar)))
    }

    override fun onIncomeEditIconClick(income: Income) {
        processIncomeAlertDialog("edit", income)
    }

    override fun onIncomeDeleteIconClick(income: Income) {
        incomeViewModel.deleteIncome(income)
        Toast.makeText(this@Dashboard, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onExpenseDeleteIconClick(expense: Expense) {
        expenseViewModel.deleteExpense(expense)
        Toast.makeText(this@Dashboard, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onExpenseEditIconClick(expense: Expense) {
        processExpenseAlertDialog("edit", expense)
    }

    fun processIncomeAlertDialog(mode: String, income: Income ?= null) {
        val addIncomeAlert = AlertDialogTemplate(this@Dashboard).CreateLottieDialog(this@Dashboard)
        addIncomeAlert.setCancelAble(true)
        addIncomeAlert.setLayout(R.layout.add_income_dialog)
        val amountField: EditText = addIncomeAlert.addChildEditText(R.id.amount)
        val description: EditText = addIncomeAlert.addChildEditText(R.id.description)
        val incomeDatePick: EditText = addIncomeAlert.addChildEditText(R.id.date)
        val saveBtn: MaterialButton = addIncomeAlert.addChildMaterialButton(R.id.saveBtn)
        val cancelBtn: MaterialButton = addIncomeAlert.addChildMaterialButton(R.id.cancelBtn)
        addIncomeAlert.showDialog()

        if (mode.equals("edit")) {
            Log.d(TAG, "processIncomeAlertDialog: Yes ${income?.incomeAmount.toString()}")
            income?.let {
                val myFormat = "MM/dd/yy"
                val dateFormat = SimpleDateFormat(myFormat)
                amountField.setText(income.incomeAmount.toString())
                description.setText(income.incomeDescription.toString())
                incomeDatePick.setText(dateFormat.format(income.date))
            }
        }

        var pickedDate: String ?= null
        val myFormat = "MM/dd/yy"
        incomeDatePick.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            val date = OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val dtFormat = SimpleDateFormat(myFormat, Locale.US)
                pickedDate = dtFormat.format(myCalendar.time)
                incomeDatePick.setText(pickedDate)
            }

            DatePickerDialog(
                this@Dashboard, date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        cancelBtn.setOnClickListener {
            addIncomeAlert.dismissDialog()
        }

        saveBtn.setOnClickListener {
            if (amountField.text.isNotEmpty() && incomeDatePick.text.isNotEmpty()) {
                var incomeObj: Income ?= null
                when (mode) {
                    "edit" -> {
                        incomeObj = Income(date = SimpleDateFormat(myFormat).parse(incomeDatePick.text.toString()),
                            incomeDescription = description.text.toString(), incomeAmount = amountField.text.toString().toLong(),
                            pushId = income!!.pushId)
                        incomeObj.id = income!!.id
                        incomeViewModel.updateIncome(incomeObj)
                        Toast.makeText(this@Dashboard, "Updated", Toast.LENGTH_SHORT).show()
                    }

                    "insert" -> {
                        incomeObj = Income(date = SimpleDateFormat(myFormat).parse(pickedDate),
                            incomeDescription = description.text.toString(), incomeAmount = amountField.text.toString().toLong(),
                            pushId = FirebaseDatabase.getInstance().reference.push().key.toString())
                        incomeViewModel.addIncome(incomeObj)
                        Toast.makeText(this@Dashboard, "Added", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@Dashboard, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            addIncomeAlert.dismissDialog()
        }

    }

    private fun processExpenseAlertDialog(mode: String, expense: Expense ?= null) {
        val addExpenseAlert = AlertDialogTemplate(this@Dashboard).CreateLottieDialog(this@Dashboard)
        addExpenseAlert.setCancelAble(true)
        addExpenseAlert.setLayout(R.layout.add_expense_dialog)
        val amountField: EditText = addExpenseAlert.addChildEditText(R.id.amount)
        val description: EditText = addExpenseAlert.addChildEditText(R.id.description)
        val expenseDatePick: EditText = addExpenseAlert.addChildEditText(R.id.date)
        val saveBtn: MaterialButton = addExpenseAlert.addChildMaterialButton(R.id.saveBtn)
        val cancelBtn: MaterialButton = addExpenseAlert.addChildMaterialButton(R.id.cancelBtn)
        addExpenseAlert.showDialog()

        if (mode.equals("edit")) {
            expense?.let {
                val myFormat = "MM/dd/yy"
                val dateFormat = SimpleDateFormat(myFormat)
                amountField.setText(expense.expenseAmount.toString())
                description.setText(expense.expenseDescription.toString())
                expenseDatePick.setText(dateFormat.format(expense.date))
            }
        }

        var pickedDate: String ?= null
        val myFormat = "MM/dd/yy"
        expenseDatePick.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            val date = OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val myFormat = "MM/dd/yy"
                val dtFormat = SimpleDateFormat(myFormat, Locale.US)
                pickedDate = dtFormat.format(myCalendar.time)
                expenseDatePick.setText(pickedDate)
            }
            DatePickerDialog(
                this@Dashboard, date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        cancelBtn.setOnClickListener {
            addExpenseAlert.dismissDialog()
        }

        saveBtn.setOnClickListener {
            if (amountField.text.isNotEmpty() && expenseDatePick.text.isNotEmpty()) {
                var expenseObj: Expense ?= null
                when (mode) {
                    "edit" -> {
                        expenseObj = Expense(date = SimpleDateFormat(myFormat).parse(expenseDatePick.text.toString()),
                            expenseDescription = description.text.toString(), expenseAmount = amountField.text.toString().toLong(),
                            pushId = expense!!.pushId)
                        expenseObj.id = expense!!.id
                        expenseViewModel.updateExpense(expenseObj)
                        Toast.makeText(this@Dashboard, "Updated", Toast.LENGTH_SHORT).show()
                    }

                    "insert" -> {
                        expenseObj = Expense(date = SimpleDateFormat(myFormat).parse(pickedDate),
                            expenseDescription = description.text.toString(), expenseAmount = amountField.text.toString().toLong(),
                            pushId = FirebaseDatabase.getInstance().reference.push().key.toString())
                        expenseViewModel.addExpense(expenseObj)
                        Toast.makeText(this@Dashboard, "Added", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@Dashboard, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            addExpenseAlert.dismissDialog()
        }
    }
}