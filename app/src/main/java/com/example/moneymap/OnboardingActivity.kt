package com.example.moneymap

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnSkip: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        tabLayout = findViewById(R.id.tabLayout)

        val onboardingItems = listOf(
            OnboardingItem(
                "Track Your Expenses",
                "Easily record and categorize your daily expenses",
                R.drawable.ic_expense
            ),
            OnboardingItem(
                "Manage Your Income",
                "Keep track of all your income sources in one place",
                R.drawable.ic_income
            ),
            OnboardingItem(
                "Financial Insights",
                "Get detailed insights into your spending patterns",
                R.drawable.ic_insights
            )
        )

        viewPager.adapter = OnboardingAdapter(onboardingItems)

        // Setup tab layout with viewpager2
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Update button text on last page
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                btnNext.text = if (position == onboardingItems.size - 1) "Get Started" else "Next"
            }
        })

        btnSkip.setOnClickListener {
            startLoginActivity()
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem == onboardingItems.size - 1) {
                startLoginActivity()
            } else {
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageResId: Int
)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)

        fun bind(item: OnboardingItem) {
            imageView.setImageResource(item.imageResId)
            titleText.text = item.title
            descriptionText.text = item.description
        }
    }
} 