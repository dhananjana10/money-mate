package com.example.moneymap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.R
import com.example.moneymap.models.Formdata1

class TransactionAdapter(
    private val transactionList: MutableList<Formdata1>,
    private val onDeleteClick: (Formdata1) -> Unit,
    private val onEditClick: (Formdata1) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transactioncard, parent, false)
        return TransactionViewHolder(view)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteButton)
        val editBtn: ImageButton = itemView.findViewById(R.id.editButton)
        val title: TextView = itemView.findViewById(R.id.titlecardtext)
        val category: TextView = itemView.findViewById(R.id.categorytext)
        val amount: TextView = itemView.findViewById(R.id.amounttext)
        val date: TextView = itemView.findViewById(R.id.Datecardtext)
        val type: TextView = itemView.findViewById(R.id.typecardtext)
        val transactionIcon: ImageView = itemView.findViewById(R.id.transactionIcon)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.title.text = transaction.title
        holder.category.text = transaction.category
        holder.amount.text = "Rs. ${transaction.amount}"
        holder.date.text = transaction.date
        holder.type.text = transaction.type

        // Set transaction icon based on category
        val iconResId = when (transaction.category.lowercase()) {
            "food and drinks" -> R.drawable.food_icon
            "transport" -> R.drawable.transport_icon
            "health" -> R.drawable.health_icon
            "education" -> R.drawable.education_icon
            "salary" -> R.drawable.salary_icon
            else -> R.drawable.other_icon
        }
        holder.transactionIcon.setImageResource(iconResId)

        // Set text color based on transaction type
        val textColor = if (transaction.type.equals("Income", ignoreCase = true)) {
            holder.itemView.context.getColor(R.color.android_green)
        } else {
            holder.itemView.context.getColor(R.color.red)
        }
        holder.amount.setTextColor(textColor)

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(transaction)
        }

        holder.editBtn.setOnClickListener {
            onEditClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    fun deleteTransaction(transaction: Formdata1) {
        val position = transactionList.indexOf(transaction)
        if (position != -1) {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateTransaction(oldTransaction: Formdata1, newTransaction: Formdata1) {
        val position = transactionList.indexOf(oldTransaction)
        if (position != -1) {
            transactionList[position] = newTransaction
            notifyItemChanged(position)
        }
    }
}