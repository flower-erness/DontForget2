package com.functions.reminder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        // ✅ Apply saved language before UI loads
        val lang = LocaleManager.getSavedLocale(this)
        LocaleManager.setLocale(this, lang)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // ✅ Redirect if not logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // ✅ Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        replaceFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_reminders -> replaceFragment(RemindersFragment())
                R.id.badges -> replaceFragment(BadgesFragment())
                R.id.nav_settings -> replaceFragment(SettingsFragment())
            }
            true
        }

        // ✅ FAB Translate button
        val fabTranslate = findViewById<FloatingActionButton>(R.id.fab_translate)
        fabTranslate.setOnClickListener {
            showLanguageDialog()
        }

        // ✅ Pre-download ML Kit models (English + Afrikaans)
        preloadLanguageModels()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ✅ Language Picker Dialog
    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Afrikaans", "Zulu", "Sesotho")
        val codes = arrayOf("en", "af", "zu", "st")

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Choose App Language")
            .setItems(languages) { _, which ->
                applyLanguage(codes[which])
            }.show()
    }

    // ✅ Save + Apply Language
    private fun applyLanguage(langCode: String) {
        LocaleManager.setLocale(this, langCode)

        // Restart activity to apply language instantly
        recreate()
    }

    // ✅ Download ML language models
    private fun preloadLanguageModels() {
        val langs = listOf("en", "af") // add more if needed

        langs.forEach { lang ->
            val model = TranslateRemoteModel.Builder(lang).build()
            val options = TranslatorOptions.Builder()
                .setSourceLanguage("en")
                .setTargetLanguage(lang)
                .build()

            val client = Translation.getClient(options)

            client.downloadModelIfNeeded()
        }
    }
}
