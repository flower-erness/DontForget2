package com.example.dontforget.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "dont_forget_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATIONS] ?: true }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = enabled
        }
    }
}
