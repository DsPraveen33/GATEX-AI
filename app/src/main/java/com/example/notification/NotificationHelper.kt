package com.example.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.model.ReminderScheduleEntity
import com.example.data.model.ReminderType
import java.text.SimpleDateFormat
import java.util.*

object NotificationHelper {

    const val CHANNEL_DAILY_PRACTICE = "channel_daily_practice"
    const val CHANNEL_MOCK_TESTS = "channel_mock_tests"
    const val CHANNEL_SPACED_REVISION = "channel_spaced_revision"
    const val CHANNEL_GENERAL = "channel_gatex_general"

    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_TYPE = "extra_type"
    const val EXTRA_TARGET_ID = "extra_target_id"
    const val EXTRA_TARGET_NAME = "extra_target_name"
    const val EXTRA_DESTINATION = "extra_destination"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val practiceChannel = NotificationChannel(
                CHANNEL_DAILY_PRACTICE,
                "Daily Practice & Quizzes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for daily question solving, syllabus drills, and streak targets"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            val mockChannel = NotificationChannel(
                CHANNEL_MOCK_TESTS,
                "Mock Tests & Exam Simulation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for scheduled 3-Hour Full GATE CSE Mocks & Subject Tests"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 250, 400)
            }

            val revisionChannel = NotificationChannel(
                CHANNEL_SPACED_REVISION,
                "Spaced Revision & Flashcards",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for reviewing due flashcards, formulas, and error trap book"
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "GATEX AI Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General study reminders and AI tutor insights"
            }

            notificationManager.createNotificationChannels(
                listOf(practiceChannel, mockChannel, revisionChannel, generalChannel)
            )
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showReminderNotification(
        context: Context,
        reminderId: Long,
        title: String,
        message: String,
        type: ReminderType,
        targetId: String = "",
        targetName: String = ""
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }

        val channelId = when (type) {
            ReminderType.DAILY_PRACTICE -> CHANNEL_DAILY_PRACTICE
            ReminderType.MOCK_TEST -> CHANNEL_MOCK_TESTS
            ReminderType.SPACED_REVISION -> CHANNEL_SPACED_REVISION
            ReminderType.CUSTOM_SESSION -> CHANNEL_GENERAL
        }

        val destination = when (type) {
            ReminderType.DAILY_PRACTICE -> "PRACTICE"
            ReminderType.MOCK_TEST -> "MOCK_TESTS"
            ReminderType.SPACED_REVISION -> "REVISION"
            ReminderType.CUSTOM_SESSION -> "DASHBOARD"
        }

        // Tap Intent
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_DESTINATION, destination)
            putExtra(EXTRA_TARGET_ID, targetId)
            putExtra(EXTRA_TYPE, type.name)
        }

        val pendingTapIntent = PendingIntent.getActivity(
            context,
            (reminderId * 10).toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Direct action button
        val actionTitle = when (type) {
            ReminderType.DAILY_PRACTICE -> "Start Daily Drill"
            ReminderType.MOCK_TEST -> "Launch Mock Exam"
            ReminderType.SPACED_REVISION -> "Review Flashcards"
            ReminderType.CUSTOM_SESSION -> "Open GATEX AI"
        }

        val actionPendingIntent = PendingIntent.getActivity(
            context,
            (reminderId * 10 + 1).toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iconRes = android.R.drawable.ic_popup_reminder

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setSummaryText(when (type) {
                        ReminderType.DAILY_PRACTICE -> "GATE 2027 CSE Practice"
                        ReminderType.MOCK_TEST -> "GATE CSE Mock Simulator"
                        ReminderType.SPACED_REVISION -> "Spaced Repetition"
                        ReminderType.CUSTOM_SESSION -> "Study Target"
                    })
            )
            .setColor(0xFF00E5FF.toInt()) // Electric Cyan
            .setPriority(
                if (type == ReminderType.MOCK_TEST || type == ReminderType.DAILY_PRACTICE)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .setContentIntent(pendingTapIntent)
            .addAction(android.R.drawable.ic_media_play, actionTitle, actionPendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun scheduleAlarm(context: Context, reminder: ReminderScheduleEntity) {
        if (!reminder.isEnabled) {
            cancelAlarm(context, reminder.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_TITLE, reminder.title)
            putExtra(EXTRA_MESSAGE, reminder.message)
            putExtra(EXTRA_TYPE, reminder.type.name)
            putExtra(EXTRA_TARGET_ID, reminder.targetId)
            putExtra(EXTRA_TARGET_NAME, reminder.targetName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = calculateNextTriggerTime(reminder)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Inexact fallback if exact alarm permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun scheduleInstantTestReminder(
        context: Context,
        title: String,
        message: String,
        type: ReminderType,
        delaySeconds: Int = 3
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val testId = (System.currentTimeMillis() % 10000) + 9000

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, testId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_TYPE, type.name)
            putExtra(EXTRA_TARGET_ID, "")
            putExtra(EXTRA_TARGET_NAME, "Test Notification")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            testId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (delaySeconds * 1000L)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            // Fallback immediate
            showReminderNotification(context, testId, title, message, type)
        }
    }

    fun cancelAlarm(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun calculateNextTriggerTime(reminder: ReminderScheduleEntity): Long {
        val now = Calendar.getInstance()

        // If a specific future date is provided (e.g. for scheduled mock tests: "2026-09-20")
        if (reminder.scheduledDate.isNotBlank()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val parsedDate = sdf.parse(reminder.scheduledDate)
                if (parsedDate != null) {
                    val targetCal = Calendar.getInstance().apply {
                        time = parsedDate
                        set(Calendar.HOUR_OF_DAY, reminder.hour)
                        set(Calendar.MINUTE, reminder.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (targetCal.timeInMillis > now.timeInMillis) {
                        return targetCal.timeInMillis
                    }
                }
            } catch (e: Exception) {
                // Fallback to recurring logic
            }
        }

        // Daily / Weekly recurrence logic
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time already passed today, advance by at least 1 day
        if (targetCal.timeInMillis <= now.timeInMillis) {
            targetCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Filter by daysOfWeek ("EVERYDAY", "WEEKDAYS", "WEEKENDS", "CUSTOM")
        when (reminder.daysOfWeek) {
            "WEEKDAYS" -> {
                while (targetCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    targetCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    targetCal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            "WEEKENDS" -> {
                while (targetCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    targetCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    targetCal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            // "EVERYDAY" or custom matches immediately
        }

        return targetCal.timeInMillis
    }
}
