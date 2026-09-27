package com.example.shaadi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShaadiApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL_MESSAGES,
				"Messages",
				NotificationManager.IMPORTANCE_DEFAULT
			).apply {
				description = "Chat message notifications"
			}
			val nm = getSystemService(NotificationManager::class.java)
			nm.createNotificationChannel(channel)
		}
	}

	companion object {
		const val NOTIFICATION_CHANNEL_MESSAGES = "messages"
	}
}
