package com.blyndchat.data.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateMapOf
import com.blyndchat.data.model.Profile
import com.blyndchat.notifications.NotificationUtils
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages connection requests between users, AI chatbots, and seed profiles.
 *
 * Rules:
 * 1. Newly created user profiles start with NO connected profiles in chats.
 * 2. AI chatbots send timed connection requests (status = "pending") into the Received section:
 *    - 1st bot (Zara / Aarav): sends request after 2 minutes.
 *    - 2nd bot (Maya / Rohan): sends request after 1 day.
 *    - 3rd bot (Aria / Kabir): sends request after 2 days.
 * 3. Incoming bot requests show up ONLY in ReceivedTab with a notification.
 * 4. Only after the user accepts (status = "accepted") does the profile appear in "Chats".
 * 5. Outgoing requests sent by user via swipe-right (status = "outgoing_pending") are accepted after ~12s.
 * 6. SharedPreferences are strictly scoped per-user (e.g. "bot_interests_{userId}") to guarantee total
 *    isolation when switching accounts.
 */
object BotInterestStore {

    const val STATUS_NONE             = "none"
    const val STATUS_PENDING          = "pending"           // Incoming request to user (shown in ReceivedTab)
    const val STATUS_OUTGOING_PENDING = "outgoing_pending"  // Outgoing request sent by user, waiting for acceptance
    const val STATUS_ACCEPTED         = "accepted"          // Accepted by both sides -> shown in ChatsTab
    const val STATUS_REJECTED         = "rejected"          // Declined / passed
    const val STATUS_BLOCKED          = "blocked"           // Blocked by user

    private const val KEY_FIRST_LOGIN_TIME = "first_login_time"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val pendingAcceptanceJobs = ConcurrentHashMap<String, Job>()
    private val scheduledBotJobs = ConcurrentHashMap<String, Job>()

    // peerId -> status ("pending", "outgoing_pending", "accepted", "rejected", "blocked")
    private val interestState = mutableStateMapOf<String, String>()
    private var activeUserId: String? = null
    private var isInitialized = false

    private data class ScheduledBot(
        val botId: String,
        val defaultName: String,
        val scheduledTime: Long
    )

    private fun getPrefs(context: Context?, userId: String?): SharedPreferences? {
        if (context == null) return null
        val effectiveUid = userId?.takeIf { it.isNotBlank() } ?: activeUserId?.takeIf { it.isNotBlank() } ?: "user_default"
        return context.getSharedPreferences("bot_interests_${effectiveUid}", Context.MODE_PRIVATE)
    }

    /**
     * Initializes the store for the specific user from SharedPreferences.
     * Clears previous user's in-memory state if switching users.
     */
    fun init(context: Context? = null, userId: String? = null) {
        val effectiveUid = userId?.takeIf { it.isNotBlank() } ?: "user_default"
        if (isInitialized && activeUserId == effectiveUid) return

        // Switching or initializing user: cancel pending background tasks and clear in-memory state
        pendingAcceptanceJobs.values.forEach { it.cancel() }
        pendingAcceptanceJobs.clear()
        scheduledBotJobs.values.forEach { it.cancel() }
        scheduledBotJobs.clear()

        interestState.clear()
        activeUserId = effectiveUid

        if (context != null) {
            // Clean up legacy un-scoped preferences to prevent dirty state from older installs
            val legacyPrefs = context.getSharedPreferences("bot_interests_prefs", Context.MODE_PRIVATE)
            if (legacyPrefs.all.isNotEmpty()) {
                legacyPrefs.edit().clear().apply()
            }

            val prefs = getPrefs(context, effectiveUid)
            val allEntries = prefs?.all ?: emptyMap()
            allEntries.forEach { (key, value) ->
                if (value is String && key != KEY_FIRST_LOGIN_TIME) {
                    interestState[key] = value
                }
            }
        }
        isInitialized = true
    }

    /**
     * Starts the bot request schedule upon first login:
     * - Zara Khan / Aarav Khan: sends first request after 2 minutes
     * - Maya Reddy / Rohan Reddy: sends request after 1 day
     * - Aria Sharma / Kabir Sharma: sends request after 2 days
     */
    fun startFirstLoginScheduleIfNeeded(
        context: Context,
        userId: String?,
        userGender: String?
    ) {
        val effectiveUid = userId?.takeIf { it.isNotBlank() } ?: "user_default"
        init(context, effectiveUid)

        val prefs = getPrefs(context, effectiveUid)
        val now = System.currentTimeMillis()

        var firstLoginTime = prefs?.getLong(KEY_FIRST_LOGIN_TIME, 0L) ?: 0L
        if (firstLoginTime <= 0L) {
            firstLoginTime = now
            prefs?.edit()?.putLong(KEY_FIRST_LOGIN_TIME, firstLoginTime)?.apply()
        }

        // Sequence requested:
        // 1st request after 2 mins (Zara)
        // 2nd request after 1 day (Maya)
        // 3rd request after 2 days (Aria)
        val zaraName = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_ZARA, userGender)?.name ?: "Zara Khan"
        val mayaName = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_MAYA, userGender)?.name ?: "Maya Reddy"
        val ariaName = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_ARIA, userGender)?.name ?: "Aria Sharma"

        val schedule = listOf(
            ScheduledBot(AiBotConfig.BOT_ID_ZARA, zaraName, firstLoginTime + 2 * 60 * 1000L),
            ScheduledBot(AiBotConfig.BOT_ID_MAYA, mayaName, firstLoginTime + 24 * 60 * 60 * 1000L),
            ScheduledBot(AiBotConfig.BOT_ID_ARIA, ariaName, firstLoginTime + 48 * 60 * 60 * 1000L)
        )

        val appContext = context.applicationContext
        schedule.forEach { bot ->
            val currentStatus = interestState[bot.botId] ?: prefs?.getString(bot.botId, null)
            if (currentStatus == STATUS_ACCEPTED || currentStatus == STATUS_REJECTED || currentStatus == STATUS_BLOCKED) {
                interestState[bot.botId] = currentStatus
                return@forEach
            }

            if (currentStatus == STATUS_PENDING) {
                interestState[bot.botId] = STATUS_PENDING
                return@forEach
            }

            if (now >= bot.scheduledTime) {
                // Scheduled arrival time has arrived
                interestState[bot.botId] = STATUS_PENDING
                prefs?.edit()?.putString(bot.botId, STATUS_PENDING)?.apply()
            } else {
                scheduledBotJobs[bot.botId]?.cancel()
                val delayMs = bot.scheduledTime - now
                val job = scope.launch {
                    try {
                        delay(delayMs)
                        val statusNow = interestState[bot.botId]
                        if (statusNow == null || statusNow == STATUS_NONE) {
                            interestState[bot.botId] = STATUS_PENDING
                            prefs?.edit()?.putString(bot.botId, STATUS_PENDING)?.apply()

                            val persona = AiBotConfig.getBotProfile(bot.botId, userGender)
                            val name = persona?.name ?: bot.defaultName
                            NotificationUtils.showMessageNotification(
                                context = appContext,
                                conversationId = BotMessageStore.conversationId(effectiveUid, bot.botId),
                                peerId = bot.botId,
                                title = name,
                                body = "$name sent you a connection request! ✨ Tap to view"
                            )
                        }
                    } catch (e: CancellationException) {
                        // Cancelled
                    }
                }
                scheduledBotJobs[bot.botId] = job
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
            val prefs = getPrefs(context, effectiveUid)
            prefs?.edit()?.putString(peerId, STATUS_OUTGOING_PENDING)?.apply()
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
            val prefs = getPrefs(context, effectiveUid)
            prefs?.edit()?.putString(botId, STATUS_ACCEPTED)?.apply()
        }

        val convoId = BotMessageStore.conversationId(effectiveUid, botId)
        BotMessageStore.initConversation(convoId)
    }

    /**
     * Declines/rejects a connection request.
     */
    fun rejectRequest(context: Context? = null, botId: String, effectiveUid: String? = null) {
        pendingAcceptanceJobs[botId]?.cancel()
        scheduledBotJobs[botId]?.cancel()
        interestState[botId] = STATUS_REJECTED
        if (context != null) {
            val prefs = getPrefs(context, effectiveUid)
            prefs?.edit()?.putString(botId, STATUS_REJECTED)?.apply()
        }
    }

    /**
     * Blocks a peer / bot. Cancels any pending jobs, sets status to STATUS_BLOCKED,
     * and removes from conversations.
     */
    fun blockPeer(context: Context? = null, peerId: String, effectiveUid: String? = null) {
        pendingAcceptanceJobs[peerId]?.cancel()
        scheduledBotJobs[peerId]?.cancel()
        interestState[peerId] = STATUS_BLOCKED
        if (context != null) {
            val prefs = getPrefs(context, effectiveUid)
            prefs?.edit()?.putString(peerId, STATUS_BLOCKED)?.apply()
        }
    }

    /** True if the peer has been blocked. */
    fun isBlocked(peerId: String): Boolean =
        getStatus(peerId) == STATUS_BLOCKED

    /**
     * Resets in-memory and scheduled jobs on logout so next login starts clean.
     */
    fun clearForLogout(context: Context? = null) {
        pendingAcceptanceJobs.values.forEach { it.cancel() }
        pendingAcceptanceJobs.clear()
        scheduledBotJobs.values.forEach { it.cancel() }
        scheduledBotJobs.clear()
        interestState.clear()
        activeUserId = null
        isInitialized = false
    }

    /**
     * Resets all bots back to "pending" requests (useful for test/demo resets).
     */
    fun resetToPending(context: Context? = null, effectiveUid: String? = null) {
        pendingAcceptanceJobs.values.forEach { it.cancel() }
        pendingAcceptanceJobs.clear()
        scheduledBotJobs.values.forEach { it.cancel() }
        scheduledBotJobs.clear()
        if (context != null) {
            val prefs = getPrefs(context, effectiveUid)
            prefs?.edit()?.clear()?.apply()
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
        activeUserId = null
        interestState.clear()
        init(null, null)
    }
}
