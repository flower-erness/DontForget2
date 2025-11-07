package com.functions.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope // 1. ADDED IMPORT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch // 1. ADDED IMPORT


class HomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var upcomingAdapter: UpcomingAdapter
    private lateinit var subscriptionAdapter: SubscriptionAdapter

    private val upcomingList = mutableListOf<UpcomingItem>()
    private val subscriptionList = mutableListOf<Subscription>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val txtHeader = view.findViewById<TextView>(R.id.txtHeader) // "Don't Forget"
        val txtUpcoming = view.findViewById<TextView>(R.id.txtUpcoming) // "Upcoming"
        val txtSubscriptions = view.findViewById<TextView>(R.id.txtSub) // "Subscriptions"

        // Translate static text
        val targetLang = LocaleManager.getSavedLocale(requireContext())
        lifecycleScope.launch {
            txtHeader.text = MLTranslator.translateText("Don't Forget", targetLang)
            txtUpcoming.text = MLTranslator.translateText("Upcoming", targetLang)
            txtSubscriptions.text = MLTranslator.translateText("Subscriptions", targetLang)
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid ?: return view

        // Recycler setup
        val upcomingRecycler = view.findViewById<RecyclerView>(R.id.upcomingRecycler)
        upcomingRecycler.layoutManager = LinearLayoutManager(requireContext())

        upcomingAdapter = UpcomingAdapter(
            upcomingList,
            onDeleteClick = { item ->
                val userId = auth.currentUser?.uid ?: return@UpcomingAdapter
                db.collection("users").document(userId)
                    .collection("upcoming")
                    .document(item.id)
                    .delete()
                    .addOnSuccessListener {
                        upcomingAdapter.removeItem(item)
                        Toast.makeText(requireContext(), "${item.name} deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to delete ${item.name}", Toast.LENGTH_SHORT).show()
                    }
            },
            onEditClick = { item ->
                val userId = auth.currentUser?.uid ?: return@UpcomingAdapter
                showEditUpcomingDialog(userId, item)
            }
        )
        upcomingRecycler.adapter = upcomingAdapter


        val subRecycler = view.findViewById<RecyclerView>(R.id.subscriptionsRecycler)
        subRecycler.layoutManager = LinearLayoutManager(requireContext())

        subscriptionAdapter = SubscriptionAdapter(
            subscriptionList,
            onDeleteClick = { sub ->
                val userId = auth.currentUser?.uid ?: return@SubscriptionAdapter
                db.collection("users").document(userId)
                    .collection("subscriptions")
                    .document(sub.id)
                    .delete()
                    .addOnSuccessListener {
                        subscriptionAdapter.removeItem(sub)
                        Toast.makeText(requireContext(), "${sub.name} deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to delete ${sub.name}", Toast.LENGTH_SHORT).show()
                    }
            },
            onEditClick = { sub ->
                val userId = auth.currentUser?.uid ?: return@SubscriptionAdapter
                showEditDialog(userId, sub)
            }
        )

        subRecycler.adapter = subscriptionAdapter


        // Fetch Firestore data
        fetchUpcoming(userId)
        fetchSubscriptions(userId)

        // Add Button
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_reminder)
        fab.setOnClickListener {
            showAddDialog(userId)
        }

        return view
    }
    private fun showEditUpcomingDialog(userId: String, item: UpcomingItem) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_item, null)

        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputName)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val amountInput = dialogView.findViewById<EditText>(R.id.inputAmount)
        val reminderInput = dialogView.findViewById<EditText>(R.id.inputReminder)

        // Initialize spinners
        typeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Upcoming", "Subscription")
        )
        categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Netflix", "Spotify", "Gym", "DStv", "Other")
        )

        // Pre-fill fields with existing data
        typeSpinner.setSelection(0) // Upcoming
        nameInput.setText(item.name)
        categorySpinner.setSelection(
            when(item.category?.lowercase() ?: "") {
                "netflix" -> 0
                "spotify" -> 1
                "gym" -> 2
                "dstv" -> 3
                else -> 4
            }
        )
        amountInput.setText(item.amount)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Upcoming")
            .setView(dialogView)
            .setPositiveButton("Save", null) // We override later to prevent auto-dismiss
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = nameInput.text.toString().trim()
                val category = categorySpinner.selectedItem.toString()
                val amount = amountInput.text.toString().trim()

                if (name.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedData = mapOf(
                    "name" to name,
                    "category" to category,
                    "amount" to amount
                )

                // Update the existing document in Firestore
                db.collection("users").document(userId)
                    .collection("upcoming")
                    .document(item.id) // Must exist!
                    .update(updatedData)
                    .addOnSuccessListener {
                        item.name = name
                        item.category = category
                        item.amount = amount
                        upcomingAdapter.updateItem(item)
                        Toast.makeText(requireContext(), "${item.name} updated!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update ${item.name}", Toast.LENGTH_SHORT).show()
                    }

            }
        }

        dialog.show()
    }


    private fun showEditDialog(userId: String, sub: Subscription) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_item, null)

        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputName)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val amountInput = dialogView.findViewById<EditText>(R.id.inputAmount)
        val reminderInput = dialogView.findViewById<EditText>(R.id.inputReminder)

        // Fill spinners and inputs with current subscription values
        typeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Upcoming", "Subscription")
        )
        categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Netflix", "Spotify", "Gym", "DStv", "Other")
        )

        typeSpinner.setSelection(1) // Subscription
        nameInput.setText(sub.name)
        categorySpinner.setSelection(
            when(sub.icon.lowercase()) {
                "netflix" -> 0
                "spotify" -> 1
                "gym" -> 2
                "dstv" -> 3
                else -> 4
            }
        )
        amountInput.setText(sub.amount)
        reminderInput.setText(sub.reminderTime)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Subscription")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val category = categorySpinner.selectedItem.toString()
                val amount = amountInput.text.toString().trim()
                val reminderTime = reminderInput.text.toString().trim()

                if (name.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedData = mapOf(
                    "name" to name,
                    "category" to category,
                    "amount" to amount,
                    "reminderTime" to reminderTime
                )

                db.collection("users").document(userId)
                    .collection("subscriptions")
                    .document(sub.id)
                    .update(updatedData)
                    .addOnSuccessListener {
                        sub.name = name
                        sub.icon = category
                        sub.amount = amount
                        sub.reminderTime = reminderTime
                        subscriptionAdapter.updateItem(sub)
                        Toast.makeText(requireContext(), "$name updated!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update $name", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Since translation is applied *inside* the dialog, we don't need a separate onViewCreated
    // if there are no other views to translate.

    private fun fetchUpcoming(userId: String) {
        db.collection("users").document(userId)
            .collection("upcoming")
            .get()
            .addOnSuccessListener { snapshot ->
                val upcomingItems = mutableListOf<UpcomingItem>()
                for (doc in snapshot.documents) {
                    val item = doc.toObject(UpcomingItem::class.java)
                    if (item != null) {
                        item.id = doc.id // ✅ assign Firestore document ID
                        upcomingItems.add(item)
                    }
                }
                upcomingAdapter.setItems(upcomingItems)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load upcoming items", Toast.LENGTH_SHORT).show()
            }
    }



    private fun fetchSubscriptions(userId: String) {
        db.collection("users").document(userId)
            .collection("subscriptions")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                subscriptionList.clear()
                for (doc in snapshot.documents) {
                    val sub = doc.toObject(Subscription::class.java)
                    if (sub != null) {
                        sub.id = doc.id   // <-- Save the Firestore document ID
                        subscriptionList.add(sub)
                    }
                }
                subscriptionAdapter.notifyDataSetChanged()
            }
    }


    private fun saveMainItem(userId: String, type: String, name: String, category: String, amount: String, reminderTime: String) {
        // Determine the Firestore collection based on the item type
        val collection = if (type == "Upcoming") "upcoming" else "subscriptions"

        // The 'category' field is crucial for the adapter to load the correct logo
        val data = hashMapOf(
            "name" to name,                  // Descriptive name (e.g., "Monthly Netflix Payment")
            "category" to category,          // Category key for logo lookup (e.g., "Netflix")
            "message" to "Payment due soon", // Static or placeholder message
            "amount" to amount,
            "reminderTime" to reminderTime,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // Save data to Firestore
        db.collection("users").document(userId)
            .collection(collection)
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$type added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error saving $type: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReminder(userId: String, name: String, type: String, amount: String, reminderTime: String) {
        if (reminderTime.isNotEmpty()) {
            val reminderData = hashMapOf(
                "text" to name, // 💡 FIX: Changed "title" to "text" to match common Reminder data class field
                "description" to "Reminder for $name payment",
                "time" to reminderTime,
                "type" to type,
                "amount" to amount,
                "createdAt" to FieldValue.serverTimestamp()
            )

            // Saving to a general 'reminders' collection, grouped by user
            db.collection("reminders")
                .document(userId)
                .collection("userReminders")
                .add(reminderData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Reminder added successfully")

                    Toast.makeText(requireContext(), "Reminder set for $name", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding reminder: ${e.message}")
                    Toast.makeText(requireContext(), "Error setting reminder: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // 🚨 UPDATED to include dynamic translation for the dialog text
    private fun showAddDialog(userId: String) {
        // Define the category options that match the keys in your adapter's 'when' block
        val CATEGORY_OPTIONS = listOf(
            "Netflix",
            "Spotify",
            "Gym",
            "DStv",
            "Other"
        )

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_item, null)

        // Find the views
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputName)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory) // 🚨 NEW
        val amountInput = dialogView.findViewById<EditText>(R.id.inputAmount)
        val reminderInput = dialogView.findViewById<EditText>(R.id.inputReminder)

        // --- SPINNER SETUP ---

        // Type Spinner setup - Items don't need translation unless stored in string resources
        typeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Upcoming", "Subscription")
        )

        // Category Spinner setup 🚨 NEW - Items don't need translation
        categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            CATEGORY_OPTIONS
        )

        // --- DATE/TIME PICKER SETUP ---

        // 🕒 Handle reminder input click (Date + Time Picker)
        reminderInput.setOnClickListener {
            val calendar = Calendar.getInstance()

            // First show DatePicker
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

                    // Then show TimePicker after date is chosen
                    TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                            val fullDateTime = "$selectedDate $selectedTime"
                            reminderInput.setText(fullDateTime)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- DIALOG BUILD & TRANSLATION ---

        val targetLang = LocaleManager.getSavedLocale(requireContext())

        // Original text for translation
        val originalTitle = "Add Item"
        val originalPositiveButton = "Save"
        val originalNegativeButton = "Cancel"
        val originalNameEmpty = "Please fill in all required fields"


        // 🔽 Build and show dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val type = typeSpinner.selectedItem.toString()
                val name = nameInput.text.toString().trim()
                val category = categorySpinner.selectedItem.toString() // 🚨 NEW: Get value from spinner
                val amount = amountInput.text.toString().trim()
                val reminderTime = reminderInput.text.toString().trim()

                // Check for empty fields, translate the error message
                lifecycleScope.launch {
                    if (name.isEmpty() || amount.isEmpty()) {
                        val translatedMessage = MLTranslator.translateText(originalNameEmpty, targetLang)
                        Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // 1. Save the main item (Passing the separate 'category' for logo lookup)
                    saveMainItem(userId, type, name, category, amount, reminderTime) // Function signature must be updated

                    // 2. Save the separate reminder if time is set (Using category as the item key)
                    saveReminder(userId, category, type, amount, reminderTime) // Function signature must be updated
                }
            }
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        // 🎯 Apply translation to AlertDialog elements
        lifecycleScope.launch {
            // Translate the dialog title
            dialog.setTitle(MLTranslator.translateText(originalTitle, targetLang))

            // Translate button text (requires dialog.show() to be called first to access buttons)
            dialog.show()

            val translatedPositive = MLTranslator.translateText(originalPositiveButton, targetLang)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = translatedPositive

            val translatedNegative = MLTranslator.translateText(originalNegativeButton, targetLang)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).text = translatedNegative
        }
    }
}