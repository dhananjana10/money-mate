package com.example.moneymap.data.model

import java.util.Date

data class Category(
    val id: Long = 0,
    val name: String,
    val color: String,
    val createdAt: Date = Date()
) 