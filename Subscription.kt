package com.functions.reminder



data class Subscription(
    var id: String = "",          // Must be here
    var name: String = "",
    var type: String = "",
    var icon: String = "",
    var amount: String = "",
    var reminderTime: String = ""
)

