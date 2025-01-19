package com.example.moneymap.Validation

sealed class ValidationResult {
    data class Invalid(var errorMessage: String):ValidationResult()
    data class isEmpty(var errorMessage:String): ValidationResult()
    object valid: ValidationResult()
}