package com.example.moneymap
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.moneymap.models.Formdata1
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Transaction : AppCompatActivity() {

    private val NOTIFICATION_ID = 101
    private val NOTIFICATION_CHANNEL_ID = "budget_channel"
    private val REQUEST_NOTIFICATION_PERMISSION = 1001
    private val NOTIFICATION_ID1 = 102
    private var totalExpenses = 0
    private var totalIncomes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction)
        createNotificationChannela()
        createNotificationChannel()

        val transaction = transactionfragment()
        val summary = summaryFragemtn()
        val setting = settingFragment()
        val budget = BudgetFragment()
        setCurrentFragment(transaction)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.topFragment, setting)
            commit()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView2)
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.transaction -> {
                    setCurrentFragment(transaction)
                    true
                }
                R.id.summary -> {
                    setCurrentFragment(summary)
                    true
                }
                R.id.budget->{
                    setCurrentFragment(budget)
                    true
                }
                else -> false
            }
        }

        // Read transactions
        val sharedPref = getSharedPreferences("transactions", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactionList", null)
        val type = object : TypeToken<ArrayList<Formdata1>>() {}.type
        val transactionList: ArrayList<Formdata1> = gson.fromJson(json, type) ?: arrayListOf()

        var tempExpenses = 0f
        var tempIncomes = 0f

        for (i in transactionList) {
            if (i.type.equals("Expense", ignoreCase = true)) {
                tempExpenses += i.amount.toFloat()
            } else {
                tempIncomes += i.amount.toFloat()
            }
        }
        var sharedPref1 = getSharedPreferences("transactions",MODE_PRIVATE)
        var budget123 = sharedPref1.getString("Budget",null)
        budget123?.toFloat()?.let {
            if(it < totalExpenses)
            {
                    checkNotificationPermission1()
            }
        }
        totalExpenses = tempExpenses.toInt()
        totalIncomes = tempIncomes.toInt()

        // Check permission
        if (totalExpenses > totalIncomes) {
            checkNotificationPermission()
        }

    }
    private fun createNotificationChannela()
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            val name = "Budget Alerts"
            val descriptionText = "Notifies when expenses exceed the budget"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("budget_channel",name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            } else {
                showBudgetExceedNotification()
            }
        } else {
            showBudgetExceedNotification()
        }
    }
    private fun checkNotificationPermission1()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            } else {
                showBudgetExceedNotification1()
            }
        } else {
            showBudgetExceedNotification1()
        }
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showBudgetExceedNotification() {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.warning)
            .setContentTitle("OverSpending Alert")
            .setContentText("You've spent Rs.$totalExpenses which is over your income Rs.$totalIncomes!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showBudgetExceedNotification1()
    {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.warning)
            .setContentTitle("Budget alert")
            .setContentText("Your expenses are higher than estimated budget")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID1, builder.build())
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showBudgetExceedNotification()
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Notifications"
            val descriptionText = "Shows budget exceed alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("transactions", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactionList", null)
        val type = object : TypeToken<ArrayList<Formdata1>>() {}.type
        val transactionList: ArrayList<Formdata1> = gson.fromJson(json, type) ?: arrayListOf()

        var tempExpenses = 0f
        var tempIncomes = 0f
        for (i in transactionList) {
            if (i.type.equals("Expense", ignoreCase = true)) {
                tempExpenses += i.amount.toFloat()
            } else {
                tempIncomes += i.amount.toFloat()
            }
        }

        totalExpenses = tempExpenses.toInt()
        totalIncomes = tempIncomes.toInt()
        var sharedPref1 = getSharedPreferences("transactions",MODE_PRIVATE)
        var budget123 = sharedPref1.getString("Budget",null)
        budget123?.toFloat()?.let {
            if(it < totalExpenses)
            {
                checkNotificationPermission1()
            }
        }
        // Check permission
        if (totalExpenses > totalIncomes) {
            checkNotificationPermission()
        }
    }
}