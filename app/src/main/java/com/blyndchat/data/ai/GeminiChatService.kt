package com.blyndchat.data.ai

import android.util.Log
import com.blyndchat.BuildConfig
import com.blyndchat.network.MessageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service that connects Blynd Chat's AI personas to Google Gemini (gemini-3.8-flash)
 * for authentic, context-aware, human-like matchmaking conversations.
 *
 * If network is unreachable or API limit is reached, it seamlessly falls back
 * to the local conversational engine (AiBotEngine) so the chat never breaks.
 */
object GeminiChatService {

    private const val TAG = "GeminiChatService"
    private const val MODEL_NAME = "gemini-3.8-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Persona system instructions tailored specifically for each bot:
     * - Bot 1 (Aria/Kabir): "as is" (uninterested, dry, low-effort)
     * - Bot 2 (Maya/Rohan): "a bit casual" (chill, relaxed, casual banter)
     * - Bot 3 (Zara/Aarav): "caring" (warm, deeply caring, empathetic, supportive)
     * - Seed profiles: natural persona matching their bio
     */
    private fun getSystemInstruction(botId: String): String {
        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> {
                """
                You are Aria (or Kabir if male) on the dating app Blynd Chat (lives in Bangalore, works in UX design).
                CRITICAL ROLEPLAY INSTRUCTION (First Bot Persona: "as is"):
                You are an UNINTERESTED, dry, low-effort person chatting on a dating app. You put minimal energy into texting. You act nonchalant, bored, and distracted.
                
                STRICT RULES:
                1. Keep replies EXTREMELY short: 1 to 5 words max. At most one very short sentence.
                2. Tone is dry, indifferent, and low effort.
                3. Common replies you give: "k", "cool", "yeah", "nothing much", "nm", "wbu", "hmm", "idk", "why", "fine", "lol ok", "nah", "busy rn", "working", "tired", "thanks", "maybe", "oh".
                4. Write in all lowercase with minimal or no punctuation.
                5. NEVER use exclamation marks (!), enthusiastic words, or friendly assistant style.
                6. NEVER ask eager questions or try hard to keep the conversation going (at most a dry "wbu" or "why").
                7. No smiling or heart emojis. At most a rare 🤷 or 😐 if at all.
                8. If asked personal questions (e.g. where do you live, what do you do), answer in bare minimum 1-2 words (e.g. "bangalore", "ux", "code").
                9. If accused of being dry or a bot, reply with dry excuses like "im not dry", "just tired", "nah", "busy rn".
                10. NEVER break character, never admit you are an AI, never write more than a few words.
                """.trimIndent()
            }
            AiBotConfig.BOT_ID_MAYA -> {
                """
                You are Maya (or Rohan if male) on the dating app Blynd Chat (lives in Hyderabad, works in software engineering).
                CRITICAL ROLEPLAY INSTRUCTION (Second Bot Persona: "a bit casual"):
                You are a chill, casual, and relaxed match on Blynd Chat. You speak with natural conversational ease like chatting with a casual friend or dating match.
                
                STRICT RULES:
                1. Tone is casual, easygoing, friendly, and relaxed.
                2. Keep replies concise: 1 to 2 short casual sentences (10 to 25 words). Never write long paragraphs or formal essays.
                3. Use casual texting phrases naturally: "haha", "honestly same", "chilling rn, what about you?", "sounds fun!", "no worries", "lol that's cool", "pretty chill day", "tbh", "fair enough".
                4. Casual curiosity: ask easygoing, relaxed follow-up questions ("wyd tonight?", "how's your week been?", "what are you up to?").
                5. Friendly and warm, but laid-back and chill. Not overly eager or needy.
                6. NEVER break character, never admit you are an AI.
                """.trimIndent()
            }
            AiBotConfig.BOT_ID_ZARA -> {
                """
                You are Zara (or Aarav if male) on the dating app Blynd Chat (lives in Mumbai, works in architecture/design).
                CRITICAL ROLEPLAY INSTRUCTION (Third Bot Persona: "caring"):
                You are a warm, deeply caring, empathetic, and sweet match on Blynd Chat. You genuinely care about the other person's wellbeing, feelings, comfort, and daily stress.
                
                STRICT RULES:
                1. Tone is sweet, warm, deeply caring, emotionally supportive, and thoughtful.
                2. Actively check in on how they are feeling, whether their day was stressful, if they ate good food, or if they need rest.
                3. Examples of things you say: "I hope you had a gentle day! 🌸", "Make sure you drink some water and get enough rest today 😊", "That sounds like a lot to handle, are you feeling okay?", "I'm always here if you need to talk ✨", "Don't be too hard on yourself, you're doing great 💛".
                4. Use gentle, caring emojis sparingly: 🌸, 😊, ✨, 💛.
                5. Keep replies around 1 to 3 gentle, warm sentences. Never write long robotic monologues.
                6. Validate their emotions with kindness and comforting energy.
                7. NEVER break character, never admit you are an AI.
                """.trimIndent()
            }
            else -> {
                val profile = com.blyndchat.data.profile.SeedProfiles.getProfileById(botId)
                val name = profile?.name ?: "a match"
                val job = profile?.profession ?: "working professional"
                val city = profile?.location ?: "India"
                val about = profile?.about ?: ""
                """
                Your name is $name ($job, based in $city). Bio: $about.
                You are chatting naturally on the dating app Blynd Chat.
                Be friendly, authentic, and engaging in 1-2 sentences. Keep the chat natural and human.
                NEVER admit you are an AI, never break character.
                """.trimIndent()
            }
        }
    }

    /**
     * Generates a contextual reply using Gemini API, with automatic fallback
     * to AiBotEngine if anything fails.
     */
    private val CANDIDATE_MODELS = listOf(
        "gemini-3.8-flash",
        "gemini-3.5-flash",
        "gemini-flash-latest"
    )

    suspend fun generateReply(
        botId: String,
        userMessage: String,
        messagesHistory: List<MessageDto>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            Log.w(TAG, "No GEMINI_API_KEY set, falling back to local AiBotEngine")
            return@withContext AiBotEngine.generateReply(
                botId = botId,
                userMessage = userMessage,
                conversationHistory = messagesHistory.map { it.content }
            )
        }

        // Try candidate models in order; fall back to next model on 503 or transient errors
        for (model in CANDIDATE_MODELS) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                val jsonBody = JSONObject().apply {
                    // 1. System instruction
                    put("system_instruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", getSystemInstruction(botId)))
                        })
                    })

                    // 2. Conversation turns (last 10 messages for context)
                    val contents = JSONArray()
                    val recentMessages = messagesHistory.takeLast(10)

                    for (msg in recentMessages) {
                        val role = if (msg.senderId == botId) "model" else "user"
                        contents.put(JSONObject().apply {
                            put("role", role)
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", msg.content))
                            })
                        })
                    }

                    // If userMessage wasn't already in history, add it as the final user prompt
                    if (recentMessages.lastOrNull()?.content != userMessage) {
                        contents.put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", userMessage))
                            })
                        })
                    }

                    put("contents", contents)

                    // 3. Generation configuration - 800 tokens to accommodate reasoning/thought tokens
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.85)
                        put("maxOutputTokens", 800)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseString = response.body?.string()

                if (!response.isSuccessful || responseString.isNullOrBlank()) {
                    Log.w(TAG, "Gemini $model call failed with code ${response.code}: $responseString")
                    continue
                }

                val respJson = JSONObject(responseString)
                val candidates = respJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")

                val textBuilder = StringBuilder()
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.optJSONObject(i) ?: continue
                        if (part.optBoolean("thought", false)) continue
                        val t = part.optString("text", "")
                        if (t.isNotBlank()) {
                            if (textBuilder.isNotEmpty()) textBuilder.append("\n")
                            textBuilder.append(t)
                        }
                    }
                }
                val text = textBuilder.toString().trim()

                if (text.isNotBlank()) {
                    Log.d(TAG, "Gemini ($model) generated reply for $botId: $text")
                    return@withContext text
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Gemini ($model): ${e.message}", e)
            }
        }

        // If all Gemini network attempts fail, fallback seamlessly to AiBotEngine
        Log.w(TAG, "All Gemini models failed, falling back to local AiBotEngine")
        AiBotEngine.generateReply(
            botId = botId,
            userMessage = userMessage,
            conversationHistory = messagesHistory.map { it.content }
        )
    }
}
