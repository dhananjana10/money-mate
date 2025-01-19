package com.example.moneymap

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.moneymap.models.Formdata1
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class summaryFragemtn : Fragment(R.layout.fragment_summary_fragemtn) {
    private lateinit var incomes: TextView
    private lateinit var expensesss: TextView
    private lateinit var balances: TextView
    private lateinit var pieChart: PieChart
    private lateinit var refreshButton: ImageButton
    private lateinit var categoryRecyclerView: RecyclerView
    private var balance: Float = 0f
    private var income: Float = 0f
    private var expenses: Float = 0f
    private var foodAndDrinks: Float = 0f
    private var transport: Float = 0f
    private var health: Float = 0f
    private var education: Float = 0f
    private var salary: Float = 0f
    private var other: Float = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            initializeViews(view)
            setupRecyclerView()
            setupRefreshButton()
            resetValues()
            loadTransactionData()
            setupPieChart(view)
        } catch (e: Exception) {
            setDefaultValues()
        }
    }

    private fun initializeViews(view: View) {
        balances = view.findViewById(R.id.edittoalbalance)
        incomes = view.findViewById(R.id.incomeedittext)
        expensesss = view.findViewById(R.id.edittotaltextexpense)
        pieChart = view.findViewById(R.id.piechart)
        refreshButton = view.findViewById(R.id.refreshButton)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
    }

    private fun setupRecyclerView() {
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)
        // TODO: Implement CategoryAdapter
    }

    private fun setupRefreshButton() {
        refreshButton.setOnClickListener {
            resetValues()
            loadTransactionData()
            setupPieChart(requireView())
        }
    }

    private fun resetValues() {
        foodAndDrinks = 0f
        transport = 0f
        health = 0f
        education = 0f
        salary = 0f
        other = 0f
        income = 0f
        expenses = 0f
        balance = 0f
    }

    private fun setDefaultValues() {
        try {
            balances.text = "Rs. 0.00"
            incomes.text = "Rs. 0.00"
            expensesss.text = "Rs. 0.00"
        } catch (e: Exception) {
            // Ignore any errors in setting default values
        }
    }

    private fun loadTransactionData() {
        try {
            val sharedPref = requireContext().getSharedPreferences("transactions", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPref.getString("transactionList", null)
            val type = object : TypeToken<ArrayList<Formdata1>>() {}.type
            val transactionList: ArrayList<Formdata1> = gson.fromJson(json, type) ?: arrayListOf()
            
            processTransactions(transactionList)
            updateUI()
        } catch (e: Exception) {
            setDefaultValues()
        }
    }

    private fun processTransactions(transactionList: ArrayList<Formdata1>) {
        for(i in transactionList) {
            if(i.type.equals("Income", ignoreCase = true)) {
                income += i.amount.toFloat()
            } else {
                expenses += i.amount.toFloat()
                processExpenseCategory(i)
            }
        }
        balance = income - expenses
    }

    private fun processExpenseCategory(transaction: Formdata1) {
        when(transaction.category.lowercase()) {
            "food and drinks" -> foodAndDrinks += transaction.amount.toFloat()
            "transport" -> transport += transaction.amount.toFloat()
            "health" -> health += transaction.amount.toFloat()
            "education" -> education += transaction.amount.toFloat()
            "salary" -> salary += transaction.amount.toFloat()
            "other" -> other += transaction.amount.toFloat()
        }
    }

    private fun updateUI() {
        try {
            balances.text = "Rs. %.2f".format(balance)
            incomes.text = "Rs. %.2f".format(income)
            expensesss.text = "Rs. %.2f".format(expenses)
        } catch (e: Exception) {
            setDefaultValues()
        }
    }

    private fun setupPieChart(view: View) {
        val entries = createPieEntries()

        if (entries.isEmpty()) {
            pieChart.visibility = View.GONE
            return
        }

        try {
            configurePieChart(entries)
        } catch (e: Exception) {
            pieChart.visibility = View.GONE
        }
    }

    private fun createPieEntries(): ArrayList<PieEntry> {
        val entries = arrayListOf<PieEntry>()
        val totalExpenses = foodAndDrinks + transport + health + education + salary + other
        
        if (totalExpenses > 0) {
            if(foodAndDrinks > 0f) entries.add(PieEntry((foodAndDrinks / totalExpenses) * 100, "Food and Drinks"))
            if(transport > 0f) entries.add(PieEntry((transport / totalExpenses) * 100, "Transport"))
            if(health > 0f) entries.add(PieEntry((health / totalExpenses) * 100, "Health"))
            if(education > 0f) entries.add(PieEntry((education / totalExpenses) * 100, "Education"))
            if(salary > 0f) entries.add(PieEntry((salary / totalExpenses) * 100, "Salary"))
            if(other > 0f) entries.add(PieEntry((other / totalExpenses) * 100, "Other"))
        }
        
        return entries
    }

    private fun configurePieChart(entries: ArrayList<PieEntry>) {
        val piedataset = PieDataSet(entries, "Expense Categories")
        
        // Set custom colors for each category
        val colors = intArrayOf(
            requireContext().getColor(R.color.food_color),
            requireContext().getColor(R.color.transport_color),
            requireContext().getColor(R.color.health_color),
            requireContext().getColor(R.color.education_color),
            requireContext().getColor(R.color.salary_color),
            requireContext().getColor(R.color.other_color)
        )
        piedataset.colors = colors.toList()
        
        // Enhanced value display
        piedataset.valueTextSize = 14f
        piedataset.valueTextColor = requireContext().getColor(R.color.text_primary)
        piedataset.valueLineColor = requireContext().getColor(R.color.text_secondary)
        piedataset.valueLinePart1Length = 0.4f
        piedataset.valueLinePart2Length = 0.4f
        piedataset.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        piedataset.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f%%", value)
            }
        }

        val pieData = PieData(piedataset)
        pieData.setValueTextSize(14f)
        pieData.setValueTextColor(requireContext().getColor(R.color.text_primary))

        pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            
            // Enhanced legend
            legend.isEnabled = true
            legend.textSize = 12f
            legend.formSize = 12f
            legend.formToTextSpace = 5f
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.xEntrySpace = 7f
            legend.yEntrySpace = 0f
            legend.yOffset = 0f
            
            // Enhanced entry labels
            setEntryLabelColor(requireContext().getColor(R.color.text_primary))
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(true)
            
            // Enhanced hole and center text
            setDrawHoleEnabled(true)
            setHoleColor(requireContext().getColor(R.color.background_light))
            setTransparentCircleRadius(55f)
            setHoleRadius(50f)
            
            setDrawCenterText(true)
            centerText = "Total Expenses\nRs. %.2f".format(expenses)
            setCenterTextSize(16f)
            setCenterTextColor(requireContext().getColor(R.color.text_primary))
            
            // Enhanced interactivity
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            
            // Enhanced animations
            animateY(1000, Easing.EaseInOutQuad)
            animateX(1000, Easing.EaseInOutQuad)
            
            // Enhanced touch handling
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {
                        val category = e.data as String
                        val percentage = e.y
                        val amount = percentage * expenses / 100
                        Toast.makeText(context, 
                            "$category: Rs. %.2f (%.1f%%)".format(amount, percentage), 
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNothingSelected() {
                    // Do nothing
                }
            })
            
            invalidate()
        }
    }
}