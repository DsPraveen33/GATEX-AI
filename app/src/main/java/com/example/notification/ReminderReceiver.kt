package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.GatexApplication
import com.example.data.model.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(NotificationHelper.EXTRA_REMINDER_ID, 0L)
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "GATE 2027 CSE Study Reminder"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "Time for your daily practice session!"
        val typeStr = intent.getStringExtra(NotificationHelper.EXTRA_TYPE) ?: ReminderType.DAILY_PRACTICE.name
        val targetId = intent.getStringExtra(NotificationHelper.EXTRA_TARGET_ID) ?: ""
        val targetName = intent.getStringExtra(NotificationHelper.EXTRA_TARGET_NAME) ?: ""

        val type = try {
            ReminderType.valueOf(typeStr)
        } catch (e: Exception) {
            ReminderType.DAILY_PRACTICE
        }

        // Post the notification
        NotificationHelper.showReminderNotification(
            context = context,
            reminderId = reminderId,
            title = title,
            message = message,
            type = type,
            targetId = targetId,
            targetName = targetName
        )

        // Reschedule next repeating occurrence and auto-rotate daily questions if daily quiz
        if (reminderId > 0) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as? GatexApplication
                    val repository = app?.repository
                    if (repository != null) {
                        // If it is the daily free quiz reminder, auto-sync and refresh today's question bank
                        if (reminderId == 99999L || type == ReminderType.DAILY_PRACTICE) {
                            try {
                                val freshDailyPack = com.example.data.local.DailyQuestionSyncData.generateFreshDailyPack()
                                repository.insertQuestions(freshDailyPack)
                            } catch (e: Exception) {
                                // Silent failover
                            }
                        }

                        val reminder = repository.getReminderById(reminderId)
                        if (reminder != null && reminder.isEnabled) {
                            NotificationHelper.scheduleAlarm(context, reminder)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
