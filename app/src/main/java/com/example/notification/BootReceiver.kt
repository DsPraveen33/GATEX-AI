package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.GatexApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as? GatexApplication
                    val repository = app?.repository
                    if (repository != null) {
                        val reminders = repository.getEnabledRemindersDirect()
                        reminders.forEach { reminder ->
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
