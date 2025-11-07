package com.functions.reminder

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val FRAGMENT_CONTAINER_ID = R.id.fragment_container
    private lateinit var txtReminders: TextView
    private lateinit var txtAppearance: TextView
    private lateinit var txtAbout: TextView
    private lateinit var txtLogout: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        txtReminders = view.findViewById(R.id.settings_text_reminders)
        txtAppearance = view.findViewById(R.id.settings_text_appearance)
        txtAbout = view.findViewById(R.id.settings_text_about)
        txtLogout = view.findViewById(R.id.settings_text_logout)

        val itemReminders = view.findViewById<View>(R.id.item_reminders)
        val itemAppearance = view.findViewById<View>(R.id.item_appearance)
        val itemAbout = view.findViewById<View>(R.id.item_about)
        val itemLogout = view.findViewById<View>(R.id.item_logout)

        // ✅ Apply translation to UI
        applyTranslation()

        itemReminders.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(FRAGMENT_CONTAINER_ID, RemindersFragment())
                .addToBackStack(null)
                .commit()
        }

        itemAppearance.setOnClickListener {
            Toast.makeText(context, "Appearance", Toast.LENGTH_SHORT).show()
        }

        itemAbout.setOnClickListener {
            Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
        }

        // ✅ Logout functionality
        itemLogout.setOnClickListener {
            auth.signOut() // Log out the user

            // Redirect to login/auth activity
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun applyTranslation() {
        val targetLang = LocaleManager.getSavedLocale(requireContext())

        val map = mapOf(
            txtReminders to "Reminders",
            txtAppearance to "Appearance",
            txtAbout to "About",
            txtLogout to "Logout"
        )

        lifecycleScope.launch {
            map.forEach { (view, original) ->
                view.text = MLTranslator.translateText(original, targetLang)
            }
        }
    }
}
