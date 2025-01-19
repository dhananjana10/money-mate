package com.example.moneymap

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moneymap.Validation.ValidationResult
import com.example.moneymap.models.BudgetData
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BudgetFragment : Fragment(R.layout.fragment_budget) {
    private lateinit var budgetInput: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var currentBudgetText: TextView
    private var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        loadCurrentBudget()
        setupListeners()
    }

    private fun initializeViews(view: View) {
        budgetInput = view.findViewById(R.id.budgetedittext)
        addButton = view.findViewById(R.id.addbudgetBtn)
        currentBudgetText = view.findViewById(R.id.currentBudgetText)
    }

    private fun loadCurrentBudget() {
        val sharedPref = requireActivity().getSharedPreferences("transactions", MODE_PRIVATE)
        val currentBudget = sharedPref.getString("Budget", "0.00")
        currentBudgetText.text = "Rs. $currentBudget"
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            val budget = budgetInput.text.toString()
            if (budget.isNotEmpty()) {
                submit(budget)
            } else {
                budgetInput.error = "Please enter a budget amount"
            }
        }
    }

    private fun submit(budget: String) {
        val budgetData = BudgetData(budget)
        val budgetValidation = budgetData.validateBudget()
        
        when(budgetValidation) {
            is ValidationResult.isEmpty -> {
                budgetInput.error = budgetValidation.errorMessage
            }
            is ValidationResult.Invalid -> {
                budgetInput.error = budgetValidation.errorMessage
            }
            is ValidationResult.valid -> {
                saveBudget(budget)
            }
        }
    }

    private fun saveBudget(budget: String) {
        val sharedPref = requireActivity().getSharedPreferences("transactions", MODE_PRIVATE)
        sharedPref.edit().putString("Budget", budget).apply()
        currentBudgetText.text = "Rs. $budget"
        budgetInput.text?.clear()
        Toast.makeText(context, "Budget updated successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadCurrentBudget()
    }
}