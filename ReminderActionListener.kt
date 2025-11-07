package com.functions.reminder

interface ReminderActionListener {
    fun onEditReminder(reminder: Reminder)
    fun onDeleteReminder(reminder: Reminder)
}