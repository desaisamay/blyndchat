package com.blyndchat.data.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.blyndchat.network.MessageDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * In-memory observable message and typing store for AI bot conversations.
 *
 * Bot messages are never sent to Supabase — they live entirely on-device.
 * Backed by Compose SnapshotStateMap and SnapshotStateList so changes
 * automatically trigger recomposition across ChatScreen and HomeScreen tabs.
 */
object BotMessageStore {

    // conversationId → observable list of messages
    private val store = mutableStateMapOf<String, SnapshotStateList<MessageDto>>()

    // conversationId → typing status
    private val typingState = mutableStateMapOf<String, Boolean>()

    /** Deterministic conversation id for a (user, bot) pair — same algorithm as HomeScreen. */
    fun conversationId(userId: String, botId: String): String =
        listOf(userId, botId).sorted().joinToString("_")

    fun getMessages(conversationId: String): List<MessageDto> =
        store[conversationId]?.toList() ?: emptyList()

    fun getObservableMessages(conversationId: String): SnapshotStateList<MessageDto> =
        store.getOrPut(conversationId) { mutableStateListOf() }

    fun addMessage(conversationId: String, message: MessageDto) {
        val list = store.getOrPut(conversationId) { mutableStateListOf() }
        list.add(message)
    }

    fun isTyping(conversationId: String): Boolean =
        typingState[conversationId] ?: false

    fun setTyping(conversationId: String, typing: Boolean) {
        typingState[conversationId] = typing
    }

    fun hasConversation(conversationId: String): Boolean =
        store[conversationId]?.isNotEmpty() == true

    /** All conversation ids that involve the given user. */
    fun conversationsFor(userId: String): List<String> =
        store.keys.filter { it.contains(userId) }

    /** The last message in a conversation, if any. */
    fun lastMessage(conversationId: String): MessageDto? =
        store[conversationId]?.lastOrNull()

    /** Total message count in a conversation (for engine context). */
    fun messageCount(conversationId: String): Int =
        store[conversationId]?.size ?: 0

    /** ISO 8601 timestamp for "now". */
    fun nowTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}
