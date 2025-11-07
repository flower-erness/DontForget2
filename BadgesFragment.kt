package com.functions.reminder

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class BadgesFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // UI elements to translate
    private lateinit var txtHeader: TextView
    private lateinit var txtTitle1: TextView
    private lateinit var txtDesc1: TextView
    private lateinit var txtTitle5: TextView
    private lateinit var txtDesc5: TextView
    private lateinit var txtTitle10: TextView
    private lateinit var txtDesc10: TextView

    // Inactive/Active colors (define these in your colors.xml or use literal values)
    private val INACTIVE_COLOR = Color.GRAY
    private val ACTIVE_COLOR_TINT = Color.parseColor("#FFD700") // Gold
    private val ACTIVE_COLOR_TEXT = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize TextViews
        txtHeader = view.findViewById(R.id.text_badges_header)
        txtTitle1 = view.findViewById(R.id.badge_title_1)
        txtDesc1 = view.findViewById(R.id.badge_desc_1)
        txtTitle5 = view.findViewById(R.id.badge_title_5)
        txtDesc5 = view.findViewById(R.id.badge_desc_5)
        txtTitle10 = view.findViewById(R.id.badge_title_10)
        txtDesc10 = view.findViewById(R.id.badge_desc_10)

        // 2. Apply translation to UI elements
        applyTranslation()

        // 3. Fetch data
        fetchSubscriptionCount(view)
    }

    private fun fetchSubscriptionCount(view: View) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Get the count of documents in the 'subscriptions' subcollection
        db.collection("users").document(userId).collection("subscriptions")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val subscriptionCount = querySnapshot.size()
                // 2. Update the UI based on the count
                updateBadgesUI(view, subscriptionCount)
            }
            .addOnFailureListener {
                // Handle error if fetching fails
                // Log.e("BadgesFragment", "Error fetching subscriptions: ${it.message}")
            }
    }

    private fun updateBadgesUI(view: View, count: Int) {
        // --- Badge 1: 1 Subscription ---
        val icon1 = view.findViewById<ImageView>(R.id.badge_icon_1)
        val title1 = view.findViewById<TextView>(R.id.badge_title_1)
        val desc1 = view.findViewById<TextView>(R.id.badge_desc_1)

        if (count >= 1) {
            setEarnedStyle(icon1, title1, desc1)
        }

        // --- Badge 2: 5 Subscriptions ---
        val icon5 = view.findViewById<ImageView>(R.id.badge_icon_5)
        val title5 = view.findViewById<TextView>(R.id.badge_title_5)
        val desc5 = view.findViewById<TextView>(R.id.badge_desc_5)

        if (count >= 5) {
            setEarnedStyle(icon5, title5, desc5)
        }

        // --- Badge 3: 10 Subscriptions ---
        val icon10 = view.findViewById<ImageView>(R.id.badge_icon_10)
        val title10 = view.findViewById<TextView>(R.id.badge_title_10)
        val desc10 = view.findViewById<TextView>(R.id.badge_desc_10)

        if (count >= 10) {
            setEarnedStyle(icon10, title10, desc10)
        }
    }

    private fun setEarnedStyle(icon: ImageView, title: TextView, description: TextView) {
        // 1. Change Icon Tint
        icon.setColorFilter(ACTIVE_COLOR_TINT)

        // 2. Change Text Colors
        title.setTextColor(ACTIVE_COLOR_TEXT)
        description.setTextColor(ACTIVE_COLOR_TEXT)
    }

    // ⭐ NEW: Translation function
    private fun applyTranslation() {
        // Map TextViews to their original English strings from the XML
        val map = mapOf(
            txtHeader to "Your Achievements",
            txtTitle1 to "Subscription Starter",
            txtDesc1 to "Register 1 Subscription.",
            txtTitle5 to "Subscription Collector",
            txtDesc5 to "Register 5 Subscriptions.",
            txtTitle10 to "Subscription Master",
            txtDesc10 to "Register 10 Subscriptions."
        )

        val targetLang = LocaleManager.getSavedLocale(requireContext())

        lifecycleScope.launch {
            map.forEach { (view, original) ->
                view.text = MLTranslator.translateText(original, targetLang)
            }
        }
    }
}