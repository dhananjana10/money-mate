package com.example.moneymap.models

import com.example.moneymap.Validation.ValidationResult

class FormData(private val title:String,private val amount:String,private val Category: String,private val Date: String,private val type: String)
{
    fun validateType(): ValidationResult{
              if(type.isEmpty())
              {
                  return ValidationResult.isEmpty("Type is empty")
              }
              else if(type=="Expense" && Category=="Salary")
              {
                  return ValidationResult.Invalid("Type is mismatch to category")
              }
              else
              {
                  return ValidationResult.valid
              }
    }
    fun validateTransactionTitle(): ValidationResult{
        if(title.isEmpty())
        {
            return ValidationResult.isEmpty("Title is empty")
        }
        else{
            return ValidationResult.valid
        }
    }
    fun validateAmount(): ValidationResult{
            val amountText = amount.toDoubleOrNull()
            if(amountText==null)
            {
                return ValidationResult.Invalid("Incorrect Amount")
            }
            else if(amount.isEmpty())
            {
                return ValidationResult.isEmpty("Amount is empty")
            }
            else{
                return ValidationResult.valid
            }
    }
    fun validateCategory(): ValidationResult{
            if(Category.isEmpty())
            {
                return ValidationResult.isEmpty("Category is empty")
            }else if(Category.equals("Category"))
            {
                return ValidationResult.Invalid("Incorrect category type")
            }
            else
            {
                return ValidationResult.valid
            }
    }
    fun validateDate(): ValidationResult{
        if(Date.isEmpty())
        {
            return ValidationResult.isEmpty("Date is empty")
        }
        else{
            return ValidationResult.valid
        }
    }
}