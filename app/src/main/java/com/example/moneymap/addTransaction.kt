package com.example.moneymap

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DialogTitle
import com.example.moneymap.Validation.ValidationResult
import com.example.moneymap.models.FormData
import com.example.moneymap.models.Formdata1

class addTransaction: AppCompatActivity() {
    var count = 0
    private var isEditing = false
    private var originalTransaction: Formdata1? = null
    lateinit var type: TextView
    lateinit var typeInput: Spinner
    lateinit var date: EditText
    lateinit var titleinput: EditText
    lateinit var amountInput: EditText
    lateinit var categoryText: TextView
    lateinit var categoryInput: Spinner
    lateinit var dateInput: EditText
    lateinit var saveButton: Button
    lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtransaction)
        
        // Initialize views
        date = findViewById<EditText>(R.id.date)
        type = findViewById(R.id.type)
        typeInput = findViewById(R.id.spinnertype)
        titleinput = findViewById(R.id.titleadd)
        amountInput = findViewById(R.id.amount)
        categoryText = findViewById(R.id.Categorytext)
        categoryInput = findViewById(R.id.category)
        dateInput = findViewById(R.id.date)
        saveButton = findViewById(R.id.saveBtn)
        titleText = findViewById(R.id.textView3)

        // Check if we're editing an existing transaction
        originalTransaction = intent.getSerializableExtra("EDIT_TRANSACTION") as? Formdata1
        if (originalTransaction != null) {
            isEditing = true
            titleText.text = "Edit Transaction"
            populateFields(originalTransaction!!)
        }

        date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(this,{_,selectedYear,selectedMonth,selectedDay->
                val formattedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                date.setText(formattedDate)
            },year,month,day)
            datePicker.show()
        }

        saveButton.setOnClickListener {
            submit()
            count = 0
        }
    }

    private fun populateFields(transaction: Formdata1) {
        titleinput.setText(transaction.title)
        amountInput.setText(transaction.amount)
        dateInput.setText(transaction.date)
        
        // Set type spinner
        val typeAdapter = typeInput.adapter as ArrayAdapter<String>
        val typePosition = typeAdapter.getPosition(transaction.type)
        if (typePosition >= 0) {
            typeInput.setSelection(typePosition)
        }
        
        // Set category spinner
        val categoryAdapter = categoryInput.adapter as ArrayAdapter<String>
        val categoryPosition = categoryAdapter.getPosition(transaction.category)
        if (categoryPosition >= 0) {
            categoryInput.setSelection(categoryPosition)
        }
    }

    fun submit() {
        val myForm = FormData(
            titleinput.text.toString(),
            amountInput.text.toString(),
            categoryInput.selectedItem.toString(),
            dateInput.text.toString(),
            typeInput.selectedItem.toString()
        )
        
        val titleValidation = myForm.validateTransactionTitle()
        val amountValidation = myForm.validateAmount()
        val categoryValidation = myForm.validateCategory()
        val dateValidation = myForm.validateDate()
        val typeValidation = myForm.validateType()

        when(typeValidation) {
            is ValidationResult.isEmpty -> type.text = typeValidation.errorMessage
            is ValidationResult.Invalid -> type.text = typeValidation.errorMessage
            is ValidationResult.valid -> {
                count++
                type.text = "type"
            }
        }

        when(titleValidation) {
            is ValidationResult.isEmpty -> titleinput.error = titleValidation.errorMessage
            is ValidationResult.Invalid -> titleinput.error = titleValidation.errorMessage
            is ValidationResult.valid -> count++
        }

        when(amountValidation) {
            is ValidationResult.isEmpty -> amountInput.error = amountValidation.errorMessage
            is ValidationResult.Invalid -> amountInput.error = amountValidation.errorMessage
            is ValidationResult.valid -> count++
        }

        when(categoryValidation) {
            is ValidationResult.Invalid -> categoryText.text = categoryValidation.errorMessage
            is ValidationResult.isEmpty -> categoryText.text = categoryValidation.errorMessage
            is ValidationResult.valid -> {
                categoryText.setText("Category")
                count++
            }
        }

        when(dateValidation) {
            is ValidationResult.isEmpty -> dateInput.error = dateValidation.errorMessage
            is ValidationResult.Invalid -> dateInput.error = dateValidation.errorMessage
            is ValidationResult.valid -> {
                dateInput.error = null
                count++
            }
        }

        if(count == 5) {
            val sharedPref = getSharedPreferences("transactions", MODE_PRIVATE)
            val gson = com.google.gson.Gson()
            val json = sharedPref.getString("transactionList", null)
            val type = object : com.google.gson.reflect.TypeToken<ArrayList<Formdata1>>(){}.type
            val transactionList: ArrayList<Formdata1> = gson.fromJson(json, type) ?: arrayListOf()
            
            val newTransaction = Formdata1(
                titleinput.text.toString(),
                amountInput.text.toString(),
                categoryInput.selectedItem.toString(),
                dateInput.text.toString(),
                typeInput.selectedItem.toString()
            )

            if (isEditing && originalTransaction != null) {
                // Update existing transaction
                val index = transactionList.indexOfFirst { 
                    it.title == originalTransaction!!.title && 
                    it.date == originalTransaction!!.date 
                }
                if (index != -1) {
                    transactionList[index] = newTransaction
                }
                // Return the edited transaction
                val resultIntent = Intent()
                resultIntent.putExtra("EDITED_TRANSACTION", newTransaction)
                setResult(RESULT_OK, resultIntent)
            } else {
                // Add new transaction
                transactionList.add(newTransaction)
            }

            val updatedJson = gson.toJson(transactionList)
            sharedPref.edit().putString("transactionList", updatedJson).apply()
            finish()
        }
    }
}