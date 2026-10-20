package com.blyndchat.data.ai

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.blyndchat.data.model.Profile
import com.blyndchat.notifications.NotificationUtils
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages connection requests between users, AI chatbots, and seed profiles.
 *
 * Rules:
 * 1. AI chatbots automatically send connection requests to the user (status = "pending") in Received section.
 * 2. When the user swipes right on a profile, status = "outgoing_pending" (request sent).
 * 3. Only after the other person/bot accepts (status = "accepted") does the profile appear in "Chats".
 * 4. Outgoing requests are accepted after a realistic delay (~12s) and post an Android system notification.
 *
 * State is observable in Compose via SnapshotStateMap and persisted in SharedPreferences.
 */
object BotInterestStore {

    const val STATUS_NONE             = "none"
    const val STATUS_PENDING          = "pending"           // Incoming request to user (shown in ReceivedTab)
    const val STATUS_OUTGOING_PENDING = "outgoing_pending"  // Outgoing request sent by user, waiting for acceptance
    const val STATUS_ACCEPTED         = "accepted"          // Accepted by both sides -> shown in ChatsTab
    const val STATUS_REJECTED         = "rejected"          // Declined / passed
    const val STATUS_BLOCKED          = "blocked"           // Blocked by user

    private const val PREFS_NAME = "bot_interests_prefs"
    private const val KEY_FIRST_LOGIN_PREFIX = "first_login_time_"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val pendingAcceptanceJobs = ConcurrentHashMap<String, Job>()
    private val scheduledBotJobs = ConcurrentHashMap<String, Job>()

    // peerId -> status ("pending", "outgoing_pending", "accepted", "rejected", "blocked")
    private val interestState = mutableStateMapOf<String, String>()
    private var isInitialized = false

    /**
     * Initializes the store from SharedPreferences.
     */
    fun init(context: Context? = null) {
        if (isInitialized) return
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val allEntries = prefs.all
            allEntries.forEach { (key, value) ->
                if (value is String && !key.startsWith("first_login_time_")) {
                    interestState[key] = value
                }
            }
        }
        isInitialized = true
    }

    /**
     * Starts the bot request schedule upon first login:
     * - Zara Khan: wait 1 min before sending first request
     * - Maya Reddy: then after 2 mins (total 3 mins from first login)
     * - Aria Sharma: then after 3 mins (total 6 mins from first login)
     */
    fun startFirstLoginScheduleIfNeeded(
        context: Context,
        userId: String?,
        userGender: String?
    ) {
        val effectiveUid = userId?.takeIf { it.isNotBlank() } ?: "user_default"
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        var firstLoginTime = prefs.getLong(KEY_FIRST_LOGIN_PREFIX + effectiveUid, 0L)
        if (firstLoginTime <= 0L) {
            firstLoginTime = now
            prefs.edit().putLong(KEY_FIRST_LOGIN_PREFIX + effectiveUid, firstLoginTime).apply()
        }

        // Sequence: 1 min for Zara, then 2 mins for Maya, then 3 mins for Aria
        val schedule = listOf(
            Triple(AiBotConfig.BOT_ID_ZARA, "Zara Khan", firstLoginTime + 60_000L),
            Triple(AiBotConfig.BOT_ID_MAYA, "Maya Reddy", firstLoginTime + 180_000L),
            Triple(AiBotConfig.BOT_ID_ARIA, "Aria Sharma", firstLoginTime + 360_000L)
        )

        val appContext = context.applicationContext
        schedule.forEach { (botId, defaultName, scheduledTime) ->
            val currentStatus = interestState[botId] ?: prefs.getString(botId, null)
            if (currentStatus == STATUS_ACCEPTED || currentStatus == STATUS_REJECTED || currentStatus == STATUS_BLOCKED) {
                interestState[botId] = currentStatus
                return@forEach
            }

            if (currentStatus == STATUS_PENDING) {
                interestState[botId] = STATUS_PENDING
                return@forEach
            }

            if (now >= scheduledTime) {
                // Scheduled arrival time has arrived
                interestState[botId] = STATUS_PENDING
                prefs.edit().putString(botId, STATUS_PENDING).apply()
            } else {
                scheduledBotJobs[botId]?.cancel()
                val delayMs = scheduledTime - now
                val job = scope.launch {
                    try {
                        delay(delayMs)
                        val statusNow = interestState[botId]
                        if (statusNow == null || statusNow == STATUS_NONE) {
                            interestState[botId] = STATUS_PENDING
                            prefs.edit().putString(botId, STATUS_PENDING).apply()

                            val persona = AiBotConfig.getBotProfile(botId, userGender)
                            val name = persona?.name ?: defaultName
                            NotificationUtils.showMessageNotification(
                                context = appContext,
                                conversationId = BotMessageStore.conversationId(effectiveUid, botId),
                                peerId = botId,
                                title = name,
                                body = "$name sent you a connection request! ✨ Tap to view"
                            )
                        }
                    } catch (e: CancellationException) {
                        // Cancelled
                    }
                }
                scheduledBotJobs[botId] = job
            }
        }
    }

    /** Returns current status for a peer ("pending", "outgoing_pending", "accepted", "rejected", "blocked", or "none"). */
    fun getStatus(peerId: String): String {
        return interestState[peerId] ?: STATUS_NONE
    }

    /** True if connection with this peer is accepted. */
    fun isAccepted(peerId: String): Boolean =
        getStatus(peerId) == STATUS_ACCEPTED

    /** True if peer sent an incoming request waiting for user in Received tab. */
    fun isPending(peerId: String): Boolean =
        getStatus(peerId) == STATUS_PENDING

    /** True if user sent an outgoing request waiting for peer to accept. */
    fun isOutgoingPending(peerId: String): Boolean =
        getStatus(peerId) == STATUS_OUTGOING_PENDING

    /** Count of pending incoming requests from bots. */
    fun pendingCount(): Int =
        AiBotConfig.ALL_BOT_IDS.count { isPending(it) }

    /** Returns the list of bot profiles that have pending requests waiting for the user in Received tab. */
    fun getPendingBots(userGender: String?): List<Profile> {
        val allBots = AiBotConfig.getBotProfiles(userGender)
        return allBots.filter { isPending(it.id) }
    }

    /** Set of peer IDs that are accepted and allowed in the Chats tab. */
    fun getAcceptedBotIds(): Set<String> {
        return interestState.filter { it.value == STATUS_ACCEPTED }.keys.toSet()
    }

    /**
     * Sends an outgoing connection request to a profile.
     * The profile does NOT immediately appear in the chat section.
     * After [delayMs] (default 12 seconds), the profile accepts, triggers a system notification,
     * and becomes visible in the Chats tab.
     */
    fun sendOutgoingRequest(
        context: Context? = null,
        peerId: String,
        peerName: String,
        effectiveUid: String,
        delayMs: Long = 12_000L,
        onAccepted: (() -> Unit)? = null
    ) {
        if (isAccepted(peerId)) return

        interestState[peerId] = STATUS_OUTGOING_PENDING
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(peerId, STATUS_OUTGOING_PENDING).apply()
        }

        pendingAcceptanceJobs[peerId]?.cancel()

        val appContext = context?.applicationContext
        val job = scope.launch {
            try {
                delay(delayMs)
                if (interestState[peerId] == STATUS_OUTGOING_PENDING) {
                    acceptRequest(appContext, peerId, effectiveUid)
                    if (appContext != null) {
                        val convoId = BotMessageStore.conversationId(effectiveUid, peerId)
                        NotificationUtils.showMessageNotification(
                            context = appContext,
                            conversationId = convoId,
                            peerId = peerId,
                            title = peerName,
                            body = "$peerName accepted your connection request! ✨ Tap to chat"
                        )
                    }
                    try {
                        withContext(Dispatchers.Main) {
                            onAccepted?.invoke()
                        }
                    } catch (e: Throwable) {
                        onAccepted?.invoke()
                    }
                }
            } catch (e: CancellationException) {
                // Job cancelled
            }
        }
        pendingAcceptanceJobs[peerId] = job
    }

    /**
     * Accepts a connection request.
     * Marks status as accepted, persists, and prepares the conversation in BotMessageStore.
     */
    fun acceptRequest(context: Context? = null, botId: String, effectiveUid: String) {
        pendingAcceptanceJobs[botId]?.cancel()
        interestState[botId] = STATUS_ACCEPTED
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(botId, STATUS_ACCEPTED).apply()
        }

        val convoId = BotMessageStore.conversationId(effectiveUid, botId)
        BotMessageStore.initConversation(convoId)
    }

    /**
     * Declines/rejects a connection request.
     */
    fun rejectRequest(context: Context? = null, botId: String) {
        pendingAcceptanceJobs[botId]?.cancel()
        scheduledBotJobs[botId]?.cancel()
        interestState[botId] = STATUS_REJECTED
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(botId, STATUS_REJECTED).apply()
        }
    }

    /**
     * Blocks a peer / bot. Cancels any pending jobs, sets status to STATUS_BLOCKED,
     * and removes from conversations.
     */
    fun blockPeer(context: Context? = null, peerId: String) {
        pendingAcceptanceJobs[peerId]?.cancel()
        scheduledBotJobs[peerId]?.cancel()
        interestState[peerId] = STATUS_BLOCKED
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(peerId, STATUS_BLOCKED).apply()
        }
    }

    /** True if the peer has been blocked. */
    fun isBlocked(peerId: String): Boolean =
        getStatus(peerId) == STATUS_BLOCKED

    /**
     * Resets all bots back to "pending" requests (useful for test/demo resets).
     */
    fun resetToPending(context: Context? = null) {
        pendingAcceptanceJobs.values.forEach { it.cancel() }
        pendingAcceptanceJobs.clear()
        scheduledBotJobs.values.forEach { it.cancel() }
        scheduledBotJobs.clear()
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
        interestState.clear()
        AiBotConfig.ALL_BOT_IDS.forEach { botId ->
            interestState[botId] = STATUS_PENDING
        }
    }

    /** Helper for testing to immediately mark a bot as pending. */
    fun markAsPendingForTesting(botId: String) {
        interestState[botId] = STATUS_PENDING
    }

    /**
     * Resets in-memory state for pure unit testing.
     */
    fun resetForTesting() {
        pendingAcceptanceJobs.values.forEach { it.cancel() }
        pendingAcceptanceJobs.clear()
        scheduledBotJobs.values.forEach { it.cancel() }
        scheduledBotJobs.clear()
        isInitialized = false
        interestState.clear()
        init(null)
    }
}
