package com.functions.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpcomingAdapter(
    private var items: MutableList<UpcomingItem>,
    private val onDeleteClick: (UpcomingItem) -> Unit,
    private val onEditClick: (UpcomingItem) -> Unit
) : RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder>() {

    class UpcomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.imgUpcomingLogo)
        val title: TextView = itemView.findViewById(R.id.txtUpcomingTitle)
        val message: TextView = itemView.findViewById(R.id.txtUpcomingMsg)
        val amount: TextView = itemView.findViewById(R.id.txtUpcomingAmount)
        val delete: ImageView = itemView.findViewById(R.id.btnDelete)
        val edit: ImageView = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming, parent, false)
        return UpcomingViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.name
        holder.message.text = item.message
        holder.amount.text = item.amount

        when (item.category?.lowercase() ?: "") {
            "netflix" -> holder.logo.setImageResource(R.drawable.ic_netflix)
            "spotify" -> holder.logo.setImageResource(R.drawable.ic_spotify)
            "gym" -> holder.logo.setImageResource(R.drawable.ic_gym)
            "dstv" -> holder.logo.setImageResource(R.drawable.ic_dstv)
            else -> holder.logo.setImageResource(R.drawable.ic_default_logo)
        }

        // Click listeners
        holder.delete.setOnClickListener { onDeleteClick(item) }
        holder.edit.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount(): Int = items.size

    /** Safely remove an item by id or object reference */
    fun removeItem(item: UpcomingItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        } else {
            // Fallback: remove by reference if ID missing
            val fallbackIndex = items.indexOf(item)
            if (fallbackIndex != -1) {
                items.removeAt(fallbackIndex)
                notifyItemRemoved(fallbackIndex)
            }
        }
    }

    /** Safely update an item by id */
    fun updateItem(item: UpcomingItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            notifyItemChanged(index)
        } else {
            // Optional: add the item if ID not found
            items.add(item)
            notifyItemInserted(items.size - 1)
        }
    }

    /** Replace all items safely */
    fun setItems(newItems: MutableList<UpcomingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
