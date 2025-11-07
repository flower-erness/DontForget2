package com.functions.reminder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // ⭐ NEW: Import ImageView for the buttons
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ⭐ NEW: Define the ReminderActionListener interface here
// This is necessary for the fragment to use the adapter and for the compiler to resolve the reference.



class ReminderAdapter(
    private val reminders: MutableList<Reminder>,
    private val onReminderToggled: (Reminder) -> Unit, // Callback function for when a reminder is checked
    // ⭐ MODIFIED: Accept the action listener in the constructor
    private val actionListener: ReminderActionListener? = null
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    // ⭐ MODIFIED: Update ViewHolder to find the new buttons
    inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reminderText: TextView = view.findViewById(R.id.text_view_reminder)
        val reminderRadio: RadioButton = view.findViewById(R.id.radio_button_reminder)
        // Find the Edit and Delete buttons from the item_reminder layout
        val btnEdit: ImageView = view.findViewById(R.id.btn_edit_reminder)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete_reminder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentReminder = reminders[position]

        holder.reminderText.text = currentReminder.text
        holder.reminderRadio.isChecked = currentReminder.isCompleted

        // Add strike-through effect for completed items
        if (currentReminder.isCompleted) {
            holder.reminderText.paintFlags = holder.reminderText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.reminderText.paintFlags = holder.reminderText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Set the click listener to update the state
        holder.reminderRadio.setOnClickListener {
            onReminderToggled(currentReminder)
        }

        // ⭐ NEW: Set Edit Button Click Listener
        holder.btnEdit.setOnClickListener {
            actionListener?.onEditReminder(currentReminder)
        }

        // ⭐ NEW: Set Delete Button Click Listener
        holder.btnDelete.setOnClickListener {
            actionListener?.onDeleteReminder(currentReminder)
        }
    }

    override fun getItemCount() = reminders.size

    // Helper function to update the list when data is fetched from Firestore
    fun updateReminders(newReminders: List<Reminder>) {
        reminders.clear()
        reminders.addAll(newReminders)
        notifyDataSetChanged()
    }
}