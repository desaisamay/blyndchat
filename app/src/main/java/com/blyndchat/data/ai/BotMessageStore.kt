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

    // conversationId → last activity timestamp in milliseconds (for sorting chats with newest on top)
    private val lastActivityTimestamps = mutableStateMapOf<String, Long>()

    // conversationId → unread message count
    private val unreadState = mutableStateMapOf<String, Int>()

    // Currently active conversation being viewed by user
    private var activeConversationId: String? = null

    /** Deterministic conversation id for a (user, bot) pair — same algorithm as HomeScreen. */
    fun conversationId(userId: String, botId: String): String =
        listOf(userId, botId).sorted().joinToString("_")

    fun getMessages(conversationId: String): List<MessageDto> =
        store[conversationId]?.toList() ?: emptyList()

    fun getObservableMessages(conversationId: String): SnapshotStateList<MessageDto> =
        store.getOrPut(conversationId) { mutableStateListOf() }

    fun addMessage(conversationId: String, message: MessageDto, isIncoming: Boolean? = null) {
        val list = store.getOrPut(conversationId) { mutableStateListOf() }
        list.add(message)
        val now = System.currentTimeMillis()
        lastActivityTimestamps[conversationId] = now

        // Check if message is incoming from peer
        val incoming = isIncoming ?: (
            message.id?.startsWith("bot_") == true ||
            (!message.id.orEmpty().startsWith("local_") && !message.id.orEmpty().startsWith("user_") && !message.senderId.startsWith("user"))
        )
        if (incoming && activeConversationId != conversationId) {
            val current = unreadState[conversationId] ?: 0
            unreadState[conversationId] = current + 1
        }
    }

    fun isTyping(conversationId: String): Boolean =
        typingState[conversationId] ?: false

    fun setTyping(conversationId: String, typing: Boolean) {
        typingState[conversationId] = typing
    }

    fun initConversation(conversationId: String, timestamp: Long = System.currentTimeMillis()) {
        store.getOrPut(conversationId) { mutableStateListOf() }
        if (!lastActivityTimestamps.containsKey(conversationId)) {
            lastActivityTimestamps[conversationId] = timestamp
        }
    }

    fun hasConversation(conversationId: String): Boolean =
        store.containsKey(conversationId)

    /** All conversation ids that involve the given user. */
    fun conversationsFor(userId: String): List<String> =
        store.keys.filter { it.contains(userId) }

    fun getLastActivityTimestamp(conversationId: String): Long =
        lastActivityTimestamps[conversationId] ?: 0L

    fun unreadCount(conversationId: String): Int =
        unreadState[conversationId] ?: 0

    fun isUnread(conversationId: String): Boolean =
        (unreadState[conversationId] ?: 0) > 0

    fun totalUnreadCount(): Int =
        unreadState.values.sum()

    fun markAsRead(conversationId: String) {
        unreadState[conversationId] = 0
    }

    fun setActiveConversation(conversationId: String?) {
        activeConversationId = conversationId
        if (conversationId != null) {
            markAsRead(conversationId)
        }
    }

    /** Removes a conversation completely (used when blocking or deleting). */
    fun removeConversation(conversationId: String) {
        store.remove(conversationId)
        typingState.remove(conversationId)
        lastActivityTimestamps.remove(conversationId)
        unreadState.remove(conversationId)
        if (activeConversationId == conversationId) {
            activeConversationId = null
        }
    }

    /** Resets in-memory store (e.g. on user logout). */
    fun clearAll() {
        store.clear()
        typingState.clear()
        lastActivityTimestamps.clear()
        unreadState.clear()
        activeConversationId = null
    }

    /** Resets in-memory store for pure unit testing. */
    fun clearForTesting() {
        clearAll()
    }

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
