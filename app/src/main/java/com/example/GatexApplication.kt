package com.example

import android.app.Application
import com.example.data.ai.AIOrchestrator
import com.example.data.local.GatexDatabase
import com.example.data.repository.GatexRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GatexApplication : Application() {

    val database: GatexDatabase by lazy {
        GatexDatabase.getDatabase(this)
    }

    val repository: GatexRepository by lazy {
        GatexRepository(database.gatexDao())
    }

    val authManager: com.example.data.firebase.FirebaseAuthManager by lazy {
        com.example.data.firebase.FirebaseAuthManager(this)
    }

    val firestoreSyncManager: com.example.data.firebase.FirestoreSyncManager by lazy {
        com.example.data.firebase.FirestoreSyncManager(this)
    }

    val orchestrator: AIOrchestrator by lazy {
        AIOrchestrator(repository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        com.example.notification.NotificationHelper.createNotificationChannels(this)
        autoScheduleDailyQuizNotification()
    }

    private fun autoScheduleDailyQuizNotification() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val existing = repository.getReminderById(99999L)
                if (existing == null) {
                    val defaultDailyQuizReminder = com.example.data.model.ReminderScheduleEntity(
                        id = 99999L,
                        title = "⚡ Daily Free Complete GATE CSE Quiz is Live!",
                        message = "Today's fresh 65Q/100M paper & solutions are ready. Solve now and boost your AIR rank!",
                        type = com.example.data.model.ReminderType.DAILY_PRACTICE,
                        targetName = "Daily Full Attempt Quiz",
                        hour = 8,
                        minute = 0,
                        daysOfWeek = "EVERYDAY",
                        isEnabled = true
                    )
                    repository.insertReminder(defaultDailyQuizReminder)
                    com.example.notification.NotificationHelper.scheduleAlarm(this@GatexApplication, defaultDailyQuizReminder)
                } else if (existing.isEnabled) {
                    com.example.notification.NotificationHelper.scheduleAlarm(this@GatexApplication, existing)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    companion object {
        lateinit var instance: GatexApplication
            private set
    }
}
