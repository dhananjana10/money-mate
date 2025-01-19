package com.example.moneymap.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MoneyMate.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val TABLE_CATEGORIES = "categories"
        private const val TABLE_BUDGETS = "budgets"

        // Common column names
        private const val KEY_ID = "id"
        private const val KEY_CREATED_AT = "created_at"

        // Transactions Table Columns
        private const val KEY_AMOUNT = "amount"
        private const val KEY_TYPE = "type"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_CATEGORY_ID = "category_id"
        private const val KEY_DATE = "date"

        // Categories Table Columns
        private const val KEY_NAME = "name"
        private const val KEY_COLOR = "color"

        // Budgets Table Columns
        private const val KEY_LIMIT = "limit_amount"
        private const val KEY_PERIOD = "period"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Categories table
        val CREATE_CATEGORIES_TABLE = """
            CREATE TABLE $TABLE_CATEGORIES (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NAME TEXT NOT NULL,
                $KEY_COLOR TEXT,
                $KEY_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Create Transactions table
        val CREATE_TRANSACTIONS_TABLE = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_AMOUNT REAL NOT NULL,
                $KEY_TYPE TEXT NOT NULL,
                $KEY_DESCRIPTION TEXT,
                $KEY_CATEGORY_ID INTEGER,
                $KEY_DATE DATE NOT NULL,
                $KEY_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($KEY_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($KEY_ID)
            )
        """.trimIndent()

        // Create Budgets table
        val CREATE_BUDGETS_TABLE = """
            CREATE TABLE $TABLE_BUDGETS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_CATEGORY_ID INTEGER,
                $KEY_LIMIT REAL NOT NULL,
                $KEY_PERIOD TEXT NOT NULL,
                $KEY_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($KEY_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($KEY_ID)
            )
        """.trimIndent()

        // Execute the SQL statements
        db.execSQL(CREATE_CATEGORIES_TABLE)
        db.execSQL(CREATE_TRANSACTIONS_TABLE)
        db.execSQL(CREATE_BUDGETS_TABLE)

        // Insert default categories
        insertDefaultCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGETS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")

        // Create tables again
        onCreate(db)
    }

    private fun insertDefaultCategories(db: SQLiteDatabase) {
        val defaultCategories = arrayOf(
            Pair("Food & Dining", "#FF5733"),
            Pair("Transportation", "#33FF57"),
            Pair("Shopping", "#3357FF"),
            Pair("Bills & Utilities", "#FF33F6"),
            Pair("Entertainment", "#33FFF6"),
            Pair("Health", "#F6FF33"),
            Pair("Income", "#00FF00")
        )

        defaultCategories.forEach { (name, color) ->
            val values = ContentValues().apply {
                put(KEY_NAME, name)
                put(KEY_COLOR, color)
            }
            db.insert(TABLE_CATEGORIES, null, values)
        }
    }
} 