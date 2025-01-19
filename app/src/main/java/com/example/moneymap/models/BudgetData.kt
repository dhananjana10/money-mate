package com.example.moneymap.models

import com.example.moneymap.Validation.ValidationResult

class BudgetData(private val budget: String) {
    fun validateBudget(): ValidationResult
    {
        var budget1 = budget.toDoubleOrNull()
        if(budget.isEmpty())
        {
            return ValidationResult.isEmpty("Budget is empty")
        }
        else if(budget1==null)
        {
            return ValidationResult.Invalid("Invaid budget")
        }

        else
        {
            return ValidationResult.valid
        }
    }
}