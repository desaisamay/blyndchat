package com.blyndchat.data.ai

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.blyndchat.data.model.Profile
import com.blyndchat.network.MessageDto

/**
 * Manages connection requests sent by AI chatbots to the user.
 *
 * Rules:
 * 1. AI chatbots automatically send connection requests to the user (status = "pending").
 * 2. Pending bot requests appear in the "Received" section.
 * 3. Only after the user accepts a request in the Received section (status = "accepted")
 *    is the user allowed to chat with that bot in the "Chats" section.
 *
 * State is observable in Compose via SnapshotStateMap and persisted in SharedPreferences.
 */
object BotInterestStore {

    const val STATUS_PENDING  = "pending"
    const val STATUS_ACCEPTED = "accepted"
    const val STATUS_REJECTED = "rejected"

    private const val PREFS_NAME = "bot_interests_prefs"

    // botId -> status ("pending", "accepted", "rejected")
    private val interestState = mutableStateMapOf<String, String>()
    private var isInitialized = false

    /**
     * Initializes the store from SharedPreferences.
     * By default, all 3 AI chatbots have sent requests to the user ("pending").
     */
    fun init(context: Context? = null) {
        if (isInitialized) return
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            AiBotConfig.ALL_BOT_IDS.forEach { botId ->
                val saved = prefs.getString(botId, null)
                interestState[botId] = saved ?: STATUS_PENDING
            }
        } else {
            AiBotConfig.ALL_BOT_IDS.forEach { botId ->
                interestState[botId] = STATUS_PENDING
            }
        }
        isInitialized = true
    }

    /** Returns current status for a bot ("pending", "accepted", "rejected"). */
    fun getStatus(botId: String): String {
        return interestState[botId] ?: STATUS_PENDING
    }

    /** True if user has accepted the bot's request. */
    fun isAccepted(botId: String): Boolean =
        getStatus(botId) == STATUS_ACCEPTED

    /** True if bot has sent a request and user hasn't accepted/rejected yet. */
    fun isPending(botId: String): Boolean =
        getStatus(botId) == STATUS_PENDING

    /** Count of pending requests from bots. */
    fun pendingCount(): Int =
        AiBotConfig.ALL_BOT_IDS.count { isPending(it) }

    /** Returns the list of bot profiles that have pending requests waiting for the user. */
    fun getPendingBots(userGender: String?): List<Profile> {
        val allBots = AiBotConfig.getBotProfiles(userGender)
        return allBots.filter { isPending(it.id) }
    }

    /** Set of bot IDs that the user has accepted. */
    fun getAcceptedBotIds(): Set<String> {
        return AiBotConfig.ALL_BOT_IDS.filter { isAccepted(it) }.toSet()
    }

    /**
     * Accepts the bot's connection request.
     * Marks status as accepted, persists, and prepares the conversation in BotMessageStore.
     */
    fun acceptRequest(context: Context? = null, botId: String, effectiveUid: String) {
        interestState[botId] = STATUS_ACCEPTED
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(botId, STATUS_ACCEPTED).apply()
        }

        // Initialize the conversation container in BotMessageStore.
        // The bot does NOT initiate the chat; the user initiates the chat after accepting.
        val convoId = BotMessageStore.conversationId(effectiveUid, botId)
        BotMessageStore.initConversation(convoId)
    }

    /**
     * Declines/rejects the bot's request.
     */
    fun rejectRequest(context: Context? = null, botId: String) {
        interestState[botId] = STATUS_REJECTED
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(botId, STATUS_REJECTED).apply()
        }
    }

    /**
     * Resets all bots back to "pending" requests (useful for test/demo resets).
     */
    fun resetToPending(context: Context? = null) {
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
        AiBotConfig.ALL_BOT_IDS.forEach { botId ->
            interestState[botId] = STATUS_PENDING
        }
    }

    /**
     * Resets in-memory state for pure unit testing.
     */
    fun resetForTesting() {
        isInitialized = false
        interestState.clear()
        init(null)
    }
}
