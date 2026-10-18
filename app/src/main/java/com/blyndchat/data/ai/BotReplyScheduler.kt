package com.blyndchat.data.ai

import android.content.Context
import android.util.Log
import com.blyndchat.network.MessageDto
import com.blyndchat.notifications.NotificationUtils
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Intelligent scheduler for AI bot replies adhering strictly to individual persona timing rules:
 *
 * 1. Bot 1 (Aria / Kabir):
 *    - Persona: "as is" (dry, uninterested, low-effort dating app chatter)
 *    - Timing: Starts typing after 10 seconds, types for ~2.5s, then delivers reply.
 *
 * 2. Bot 2 (Maya / Rohan):
 *    - Persona: "a bit casual" (chill, relaxed, casual banter, slang)
 *    - Timing: Replies after 10 minutes (600,000ms), shows typing indicator right before delivery.
 *
 * 3. Bot 3 (Zara / Aarav):
 *    - Persona: "caring" (warm, deeply caring, empathetic, supportive, sweet)
 *    - Timing: Replies after every 1 to 3 minutes (60,000ms to 180,000ms).
 *
 * 4. Seed Profiles:
 *    - Realistic casual dating app response (~5s total).
 *
 * Runs on an application-level CoroutineScope so timers survive Composable lifecycle changes
 * and post system notifications even if the user navigates away or switches apps.
 */
object BotReplyScheduler {

    private const val TAG = "BotReplyScheduler"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val activeJobs = ConcurrentHashMap<String, Job>()

    /**
     * Determines timing parameters for a given bot.
     * Returns Pair(preTypingDelayMs, typingDurationMs).
     */
    fun getTimingConfig(botId: String): Pair<Long, Long> {
        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> {
                // First bot: starts typing after exactly 10 seconds, types for 2.5s
                Pair(10_000L, 2_500L)
            }
            AiBotConfig.BOT_ID_MAYA -> {
                // Second bot: replies after 10 minutes (600s). Types during last 4s.
                val totalMs = 10 * 60 * 1000L // 600,000 ms
                val typingMs = 4_000L
                Pair(totalMs - typingMs, typingMs)
            }
            AiBotConfig.BOT_ID_ZARA -> {
                // Third bot: replies after every 1 to 3 minutes (60s to 180s)
                val totalMs = Random.nextLong(60_000L, 180_001L)
                val typingMs = 3_500L
                Pair((totalMs - typingMs).coerceAtLeast(0L), typingMs)
            }
            else -> {
                // Seed profiles / fallback: quick casual response
                Pair(3_000L, 2_000L)
            }
        }
    }

    /**
     * Dispatches an asynchronous bot reply according to the bot's timing rules.
     */
    fun scheduleReply(
        context: Context,
        conversationId: String,
        botId: String,
        userMessage: String,
        chatTitle: String,
        receiverId: String,
        customTiming: Pair<Long, Long>? = null
    ) {
        // Cancel any previous pending response for this conversation
        activeJobs[conversationId]?.cancel()

        val appContext = context.applicationContext
        val (preTypingDelay, typingDuration) = customTiming ?: getTimingConfig(botId)

        val job = scope.launch {
            try {
                Log.d(TAG, "Scheduled reply for $botId ($chatTitle): preTyping=${preTypingDelay}ms, typing=${typingDuration}ms")

                // 1. Wait pre-typing delay (e.g. 10s for Aria, ~9m56s for Maya, ~1-3m for Zara)
                if (preTypingDelay > 0) {
                    delay(preTypingDelay)
                }

                // 2. Start typing indicator
                BotMessageStore.setTyping(conversationId, true)
                Log.d(TAG, "Started typing for $botId in $conversationId")

                // 3. Prepare response while typing
                val currentHistory = BotMessageStore.getMessages(conversationId)
                val reply = GeminiChatService.generateReply(
                    botId = botId,
                    userMessage = userMessage,
                    messagesHistory = currentHistory
                )

                // 4. Wait out the typing animation
                if (typingDuration > 0) {
                    delay(typingDuration)
                }

                // 5. Stop typing indicator
                BotMessageStore.setTyping(conversationId, false)

                // 6. Add bot message to store
                val botMsg = MessageDto(
                    id = "bot_${System.currentTimeMillis()}",
                    conversationId = conversationId,
                    senderId = botId,
                    receiverId = receiverId,
                    content = reply,
                    createdAt = BotMessageStore.nowTimestamp()
                )
                BotMessageStore.addMessage(conversationId, botMsg)
                Log.d(TAG, "Delivered reply for $botId in $conversationId: $reply")

                // 7. Post system notification
                NotificationUtils.showMessageNotification(
                    context = appContext,
                    conversationId = conversationId,
                    peerId = botId,
                    title = chatTitle,
                    body = reply
                )
            } catch (e: CancellationException) {
                BotMessageStore.setTyping(conversationId, false)
                Log.d(TAG, "Bot reply for $conversationId was cancelled")
            } catch (e: Exception) {
                BotMessageStore.setTyping(conversationId, false)
                Log.e(TAG, "Error generating bot reply: ${e.message}", e)
            } finally {
                activeJobs.remove(conversationId)
            }
        }

        activeJobs[conversationId] = job
    }

    /** Cancel pending reply for a conversation (e.g. if cleared). */
    fun cancelPending(conversationId: String) {
        activeJobs[conversationId]?.cancel()
        activeJobs.remove(conversationId)
        BotMessageStore.setTyping(conversationId, false)
    }
}
