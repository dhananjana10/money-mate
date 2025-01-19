package com.example.moneymap

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var mainlayout:View
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)
        mainlayout = findViewById(R.id.mainlayout)
        mainlayout.setOnTouchListener {_,_->
                val intent = Intent(this,Transaction::class.java)
                startActivity(intent)
                true
        }
    }

}