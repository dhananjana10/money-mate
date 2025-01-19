package com.example.moneymap.data.model

import java.util.Date

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val period: BudgetPeriod,
    val createdAt: Date = Date()
)

enum class BudgetPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
} 