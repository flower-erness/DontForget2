package com.example.dontforget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dontforget.data.settings.SettingsRepository
import com.example.dontforget.worker.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app.applicationContext)
    val notifications: StateFlow<Boolean> =
        repo.notificationsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repo.setNotifications(enabled)
            if (enabled) ReminderScheduler.schedule(getApplication())
            else ReminderScheduler.cancel(getApplication())
        }
    }
}
