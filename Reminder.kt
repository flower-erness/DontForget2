package com.functions.reminder



// Note: Ensure your data classes match the fields in your Firestore documents
data class Reminder(
    var id: String = "",
    val text: String = "",
    var isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis() // Useful for sorting
)