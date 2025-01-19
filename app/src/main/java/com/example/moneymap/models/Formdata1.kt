package com.example.moneymap.models

import java.io.Serializable

data class Formdata1(
    val title: String,
    val amount: String,
    val category: String,
    val date: String,
    val type: String
) : Serializable