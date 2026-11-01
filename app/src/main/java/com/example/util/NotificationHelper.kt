package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_REMINDER_ID = "my_day_reminder_channel"
        const val NOTIFICATION_TASK_ID = 1002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER_ID,
                "Daily Planner Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily progress updates and celebration alerts"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    private fun canPostNotification(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun notifyAllTasksCompleted() {
        vibrateDevice()

        if (!canPostNotification()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground_1793511511949)
            .setContentTitle("All Tasks Completed!")
            .setContentText("You've finished everything on your list for today! Outstanding job.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_TASK_ID, notification)
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    private fun vibrateDevice() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 300, 150, 300),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 300, 150, 300),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 300, 150, 300), -1)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration is not supported or permission denied
        }
    }
}
