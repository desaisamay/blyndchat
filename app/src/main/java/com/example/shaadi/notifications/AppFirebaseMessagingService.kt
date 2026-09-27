package com.example.shaadi.notifications

import android.util.Log
import com.example.shaadi.data.auth.CredentialStore
import com.example.shaadi.network.DeviceTokenDto
import com.example.shaadi.network.DeviceTokensService
import com.example.shaadi.network.SupabaseRestApiClient
import com.example.shaadi.notifications.NotificationUtils.showMessageNotification
import com.example.shaadi.util.JwtUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Try to upsert the token to Supabase if the user is logged in.
        Thread {
            try {
                val store = CredentialStore(applicationContext)
                val jwt = store.getToken()
                if (jwt.isNullOrBlank()) {
                    Log.d("FCM", "No JWT available; will upsert when user logs in")
                    return@Thread
                }
                val userId = JwtUtils.getUserIdFromToken(jwt)
                if (userId.isNullOrBlank()) {
                    Log.d("FCM", "Could not derive userId from JWT; skipping upsert")
                    return@Thread
                }
                val retrofit = SupabaseRestApiClient.authedRetrofit(jwt)
                val svc = retrofit.create(DeviceTokensService::class.java)
                val resp = runBlocking { svc.upsert(listOf(DeviceTokenDto(userId = userId, token = token))) }
                if (resp.isSuccessful) {
                    Log.d("FCM", "Token upserted for user=$userId")
                } else {
                    Log.w("FCM", "Upsert failed http=${resp.code()} body=${resp.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                Log.w("FCM", "Upsert error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                Log.w("FCM", "Upsert exception: ${e.message}")
            }
        }.start()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val conversationId = data["conversation_id"] ?: return
        val peerId = data["peer_id"]
        val content = data["content"] ?: "New message"
        val title = data["title"] ?: "New message"
        showMessageNotification(
            context = this,
            conversationId = conversationId,
            peerId = peerId,
            title = title,
            body = content
        )
    }
}
