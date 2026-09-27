package com.example.shaadi.data.unread

import android.content.Context

class UnreadStore(context: Context) {
    private val prefs = context.getSharedPreferences("unread_store", Context.MODE_PRIVATE)

    fun getLastRead(conversationId: String): String? =
        prefs.getString(key(conversationId), null)

    fun setLastRead(conversationId: String, timestampIso: String) {
        prefs.edit().putString(key(conversationId), timestampIso).apply()
    }

    fun isUnread(conversationId: String, lastMessageAtIso: String): Boolean {
        val lastRead = getLastRead(conversationId) ?: return false
        // ISO-8601 in UTC (e.g., 2025-12-22T02:50:58.422Z) compares lexicographically
        return lastMessageAtIso > lastRead
    }

    private fun key(conversationId: String) = "last_read_$conversationId"
}
