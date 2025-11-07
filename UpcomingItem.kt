package com.functions.reminder



data class UpcomingItem(
    var id: String = "",        // Firestore document ID
    var name: String = "",
    var message: String = "",
    var category: String? = "",
    var amount: String = ""
)
