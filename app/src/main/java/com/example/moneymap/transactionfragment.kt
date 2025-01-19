package com.example.moneymap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.adapter.TransactionAdapter
import com.example.moneymap.models.Formdata1
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class transactionfragment : Fragment(R.layout.fragment_transactionfragment) {
    lateinit var transactionadapter: TransactionAdapter
    lateinit var addButton: Button
    lateinit var recyclerView: RecyclerView
    private val EDIT_TRANSACTION_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview)
        addButton = view.findViewById<Button>(R.id.button)
        addButton.setOnClickListener {
            val intent = Intent(requireActivity(), addTransaction::class.java)
            startActivity(intent)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val sharedPref = requireContext().getSharedPreferences("transactions", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactionList", null)
        val type = object : TypeToken<ArrayList<Formdata1>>() {}.type
        val transactionList: ArrayList<Formdata1> = gson.fromJson(json, type) ?: arrayListOf()
        
        transactionadapter = TransactionAdapter(
            transactionList.toMutableList(),
            { transaction -> deleteTransaction(transaction) },
            { transaction -> editTransaction(transaction) }
        )
        recyclerView.adapter = transactionadapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun deleteTransaction(transaction: Formdata1) {
        val sharedPref = requireContext().getSharedPreferences("transactions", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactionList", null)
        val type = object : TypeToken<MutableList<Formdata1>>() {}.type
        val list: MutableList<Formdata1> = gson.fromJson(json, type) ?: mutableListOf()
        list.remove(transaction)
        val newJson = gson.toJson(list)
        sharedPref.edit().putString("transactionList", newJson).apply()
        transactionadapter.deleteTransaction(transaction)
    }

    private fun editTransaction(transaction: Formdata1) {
        val intent = Intent(requireActivity(), addTransaction::class.java)
        intent.putExtra("EDIT_TRANSACTION", transaction as java.io.Serializable)
        startActivityForResult(intent, EDIT_TRANSACTION_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TRANSACTION_REQUEST && data != null) {
            val editedTransaction = data.getSerializableExtra("EDITED_TRANSACTION") as? Formdata1
            if (editedTransaction != null) {
                val sharedPref = requireContext().getSharedPreferences("transactions", Context.MODE_PRIVATE)
                val gson = Gson()
                val json = sharedPref.getString("transactionList", null)
                val type = object : TypeToken<MutableList<Formdata1>>() {}.type
                val list: MutableList<Formdata1> = gson.fromJson(json, type) ?: mutableListOf()
                
                val index = list.indexOfFirst { it.title == editedTransaction.title && it.date == editedTransaction.date }
                if (index != -1) {
                    list[index] = editedTransaction
                    val newJson = gson.toJson(list)
                    sharedPref.edit().putString("transactionList", newJson).apply()
                    setupRecyclerView()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}