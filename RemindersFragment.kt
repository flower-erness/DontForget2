package com.functions.reminder

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue
import android.widget.EditText // Required for the Edit dialog
import androidx.lifecycle.lifecycleScope // 1. ADDED IMPORT
import kotlinx.coroutines.launch // 1. ADDED IMPORT


class RemindersFragment : Fragment(), ReminderActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        if (userId == null) {
            // Apply translation to Toast message
            lifecycleScope.launch {
                val targetLang = LocaleManager.getSavedLocale(requireContext())
                val translatedMessage = MLTranslator.translateText("User not logged in.", targetLang)
                Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_LONG).show()
            }
            return null
        }

        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeUserId = userId ?: return

        recyclerView = view.findViewById(R.id.reminders_recycler_view)

        // ⭐ FIX 3: Initialize adapter correctly, passing 'this' as the action listener
        reminderAdapter = ReminderAdapter(
            mutableListOf(),
            onReminderToggled = { reminder ->
                toggleReminderCompletion(safeUserId, reminder)
            },
            actionListener = this // Pass the fragment as the action listener
        )
        recyclerView.adapter = reminderAdapter

        loadReminders(safeUserId)
    }

    // --- REMINDER ACTION LISTENER IMPLEMENTATION ---

    override fun onEditReminder(reminder: Reminder) {
        // ⭐ Implements the edit action
        showReminderEditDialog(reminder)
    }

    override fun onDeleteReminder(reminder: Reminder) {
        // ⭐ Implements the delete action
        showDeleteConfirmationDialog(reminder)
    }

    // --- CRUD FUNCTIONS (Provided in previous steps) ---

    private fun loadReminders(userId: String) {
        db.collection("reminders").document(userId).collection("userReminders")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("RemindersFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val remindersList = snapshot.documents.map { document ->
                        val reminder = document.toObject(Reminder::class.java)!!
                        reminder.id = document.id
                        reminder
                    }
                    reminderAdapter.updateReminders(remindersList)
                }
            }
    }

    private fun toggleReminderCompletion(userId: String, reminder: Reminder) {
        val newStatus = !reminder.isCompleted

        db.collection("reminders").document(userId).collection("userReminders").document(reminder.id)
            .update("isCompleted", newStatus)
            .addOnSuccessListener {
                Log.d("RemindersFragment", "Reminder status updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("RemindersFragment", "Error updating document", e)
            }
    }

    // --- DELETE LOGIC (UPDATED FOR TRANSLATION) ---

    private fun showDeleteConfirmationDialog(reminder: Reminder) {
        val originalTitle = "Delete Reminder"
        val originalMessage = "Are you sure you want to permanently delete this reminder?"
        val originalPositiveButton = "Delete"
        val originalNegativeButton = "Cancel"

        // Create the dialog builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setPositiveButton(originalPositiveButton) { _, _ ->
                performDeletion(reminder)
            }
            .setNegativeButton(originalNegativeButton, null)

        // Translate and show the dialog
        lifecycleScope.launch {
            val targetLang = LocaleManager.getSavedLocale(requireContext())

            val translatedTitle = MLTranslator.translateText(originalTitle, targetLang)
            val translatedMessage = MLTranslator.translateText(originalMessage, targetLang)

            val dialog = dialogBuilder
                .setTitle(translatedTitle)
                .setMessage(translatedMessage)
                .create()

            dialog.show()

            // Translate button text after showing the dialog
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.text = MLTranslator.translateText(originalPositiveButton, targetLang)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.text = MLTranslator.translateText(originalNegativeButton, targetLang)
        }
    }

    private fun performDeletion(reminder: Reminder) {
        val safeUserId = userId ?: return

        db.collection("reminders").document(safeUserId).collection("userReminders").document(reminder.id)
            .delete()
            .addOnSuccessListener {
                lifecycleScope.launch {
                    val targetLang = LocaleManager.getSavedLocale(requireContext())
                    val translatedMessage = MLTranslator.translateText("Reminder deleted!", targetLang)
                    Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                lifecycleScope.launch {
                    val targetLang = LocaleManager.getSavedLocale(requireContext())
                    val translatedMessage = MLTranslator.translateText("Error deleting reminder.", targetLang)
                    Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                }
                Log.w("RemindersFragment", "Error deleting document", e)
            }
    }

    // --- EDIT LOGIC (UPDATED FOR TRANSLATION) ---

    private fun showReminderEditDialog(reminder: Reminder) {
        val safeUserId = userId ?: return
        val originalTitle = "Edit Reminder"
        val originalPositiveButton = "Save"
        val originalNegativeButton = "Cancel"
        val originalEmptyError = "Reminder text cannot be empty."

        // Create a simple EditText programmatically for the dialog
        val input = EditText(requireContext()).apply {
            setText(reminder.text) // Pre-populate with current text
            setSingleLine(false)
            isVerticalScrollBarEnabled = true
            maxLines = 5
            // Add padding for better visual appearance inside the dialog
            setPadding(50, 20, 50, 20)
        }

        // Create the dialog builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(input)
            .setPositiveButton(originalPositiveButton) { dialog, _ ->
                val newText = input.text.toString().trim()

                if (newText.isEmpty()) {
                    lifecycleScope.launch {
                        val targetLang = LocaleManager.getSavedLocale(requireContext())
                        val translatedMessage = MLTranslator.translateText(originalEmptyError, targetLang)
                        Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                    }
                    return@setPositiveButton
                }

                performReminderUpdate(safeUserId, reminder.id, newText)
                dialog.dismiss()
            }
            .setNegativeButton(originalNegativeButton) { dialog, _ ->
                dialog.cancel()
            }

        // Translate and show the dialog
        lifecycleScope.launch {
            val targetLang = LocaleManager.getSavedLocale(requireContext())
            val translatedTitle = MLTranslator.translateText(originalTitle, targetLang)

            val dialog = dialogBuilder
                .setTitle(translatedTitle)
                .create()

            dialog.show()

            // Translate button text after showing the dialog
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.text = MLTranslator.translateText(originalPositiveButton, targetLang)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.text = MLTranslator.translateText(originalNegativeButton, targetLang)
        }
    }

    private fun performReminderUpdate(userId: String, reminderId: String, newText: String) {
        if (reminderId.isEmpty()) {
            lifecycleScope.launch {
                val targetLang = LocaleManager.getSavedLocale(requireContext())
                val translatedMessage = MLTranslator.translateText("Error: Reminder ID is missing.", targetLang)
                Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
            }
            return
        }

        val updateData = hashMapOf(
            "text" to newText,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        db.collection("reminders").document(userId).collection("userReminders").document(reminderId)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                lifecycleScope.launch {
                    val targetLang = LocaleManager.getSavedLocale(requireContext())
                    val translatedMessage = MLTranslator.translateText("Reminder updated successfully!", targetLang)
                    Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                lifecycleScope.launch {
                    val targetLang = LocaleManager.getSavedLocale(requireContext())
                    val translatedMessage = MLTranslator.translateText("Error updating reminder.", targetLang)
                    Toast.makeText(requireContext(), translatedMessage, Toast.LENGTH_SHORT).show()
                }
                Log.w("RemindersFragment", "Error updating document: $e")
            }
    }
}