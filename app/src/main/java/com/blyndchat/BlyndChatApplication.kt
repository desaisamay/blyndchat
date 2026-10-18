package com.blyndchat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlyndChatApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		instance = this
		createNotificationChannel()
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL_MESSAGES,
				"Messages",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Chat message notifications"
				enableVibration(true)
				vibrationPattern = longArrayOf(0, 250, 150, 250)
			}
			val nm = getSystemService(NotificationManager::class.java)
			nm.createNotificationChannel(channel)
		}
	}

	companion object {
		lateinit var instance: BlyndChatApplication
			private set
		const val NOTIFICATION_CHANNEL_MESSAGES = "messages"
	}
}
