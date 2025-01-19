package com.example.moneymap

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class FinancialSummaryActivity : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var summaryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_summary)

        // Initialize views
        pieChart = findViewById(R.id.incomePieChart)
        summaryText = findViewById(R.id.summaryText)

        // Setup pie chart
        setupPieChart()
        
        // Load and display data
        loadIncomeData()
    }

    private fun setupPieChart() {
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400)
        pieChart.legend.isEnabled = true
    }

    private fun loadIncomeData() {
        // Sample data - replace with actual data from your database
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Salary"))
        entries.add(PieEntry(30f, "Investments"))
        entries.add(PieEntry(20f, "Freelance"))
        entries.add(PieEntry(10f, "Other"))

        val dataSet = PieDataSet(entries, "Income Categories")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Add colors
        val colors = ArrayList<Int>()
        for (color in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(color)
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data
        pieChart.invalidate()

        // Update summary text
        val totalIncome = entries.sumOf { it.value.toDouble() }
        summaryText.text = "Total Income: $${totalIncome.toInt()}"
    }
} 