package com.functions.reminder

import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation
import kotlinx.coroutines.tasks.await

object MLTranslator {

    suspend fun translateText(text: String, targetLang: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage("en")  // Your app default text language
            .setTargetLanguage(targetLang)
            .build()

        val translator = Translation.getClient(options)

        return try {
            translator.downloadModelIfNeeded().await()
            translator.translate(text).await()
        } catch (e: Exception) {
            text // fallback original text
        }
    }
}
