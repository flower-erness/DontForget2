package com.functions.reminder



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SubscriptionAdapter(
    private val subscriptions: MutableList<Subscription>,
    private val onDeleteClick: (Subscription) -> Unit,
    private val onEditClick: (Subscription) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.imgSubLogo)
        val name: TextView = itemView.findViewById(R.id.txtSubName)
        val type: TextView = itemView.findViewById(R.id.txtSubType)
        val delete: ImageView = itemView.findViewById(R.id.btnDelete)
        val edit: ImageView = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val item = subscriptions[position]
        holder.name.text = item.name
        holder.type.text = item.type

        // Load logo based on icon key
        when (item.icon.lowercase()) {
            "netflix" -> holder.logo.setImageResource(R.drawable.ic_netflix)
            "spotify" -> holder.logo.setImageResource(R.drawable.ic_spotify)
            "gym" -> holder.logo.setImageResource(R.drawable.ic_gym)
            "dstv" -> holder.logo.setImageResource(R.drawable.ic_dstv)
            else -> holder.logo.setImageResource(R.drawable.ic_default_logo)
        }

        holder.delete.setOnClickListener { onDeleteClick(item) }
        holder.edit.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount(): Int = subscriptions.size

    fun removeItem(sub: Subscription) {
        val index = subscriptions.indexOf(sub)
        if (index != -1) {
            subscriptions.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateItem(sub: Subscription) {
        val index = subscriptions.indexOfFirst { it.id == sub.id }
        if (index != -1) {
            subscriptions[index] = sub
            notifyItemChanged(index)
        }
    }
}
