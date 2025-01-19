package com.example.moneymap.data.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val description: String?,
    val categoryId: Long?,
    val date: Date,
    val createdAt: Date = Date()
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 