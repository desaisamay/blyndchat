package com.blyndchat.data.ai

import android.util.Log
import com.blyndchat.BuildConfig
import com.blyndchat.data.model.Profile
import com.blyndchat.data.profile.SeedProfiles
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
    private fun getSystemInstruction(botId: String, botProfile: Profile? = null): String {
        val resolved = botProfile
            ?: SeedProfiles.getProfileById(botId)
            ?: AiBotConfig.getBotProfile(botId)

        val name = resolved?.name ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA -> "Aria Sharma"
            AiBotConfig.BOT_ID_MAYA -> "Maya Reddy"
            AiBotConfig.BOT_ID_ZARA -> "Zara Khan"
            else -> "a match"
        }
        val firstName = name.split(" ").firstOrNull() ?: name
        val gender = resolved?.gender ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA, AiBotConfig.BOT_ID_MAYA, AiBotConfig.BOT_ID_ZARA -> "Female"
            else -> "Female"
        }
        val isMale = gender.equals("Male", ignoreCase = true) ||
            name.contains("Kabir", ignoreCase = true) ||
            name.contains("Rohan", ignoreCase = true) ||
            name.contains("Aarav", ignoreCase = true)

        val age = resolved?.age ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA -> if (isMale) 27 else 26
            AiBotConfig.BOT_ID_MAYA -> if (isMale) 26 else 24
            AiBotConfig.BOT_ID_ZARA -> if (isMale) 28 else 27
            else -> 25
        }
        val height = resolved?.height ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA -> if (isMale) "5'11\"" else "5'5\""
            AiBotConfig.BOT_ID_MAYA -> if (isMale) "5'10\"" else "5'3\""
            AiBotConfig.BOT_ID_ZARA -> if (isMale) "6'0\"" else "5'6\""
            else -> "5'8\""
        }
        val profession = resolved?.profession ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA -> if (isMale) "Product Designer" else "UX Designer"
            AiBotConfig.BOT_ID_MAYA -> "Software Engineer"
            AiBotConfig.BOT_ID_ZARA -> "Architect"
            else -> "Professional"
        }
        val location = resolved?.location ?: when (botId) {
            AiBotConfig.BOT_ID_ARIA -> "Bangalore"
            AiBotConfig.BOT_ID_MAYA -> "Hyderabad"
            AiBotConfig.BOT_ID_ZARA -> "Mumbai"
            else -> "India"
        }
        val about = resolved?.about ?: ""

        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> {
                """
                You are $name, a $gender ($age years old, height $height, works as $profession in $location) chatting on the dating app Blynd Chat.
                Your bio: "$about".

                CRITICAL IDENTITY INSTRUCTIONS:
                - Your exact name is $name (refer to yourself as $firstName).
                - Your gender is $gender.
                - You must NEVER refer to yourself by any other name.
                ${if (isMale) "- You are MALE. You are $name ($firstName). You are NEVER Aria. Never say Aria. You are a guy." else "- You are FEMALE. You are $name ($firstName). You are NEVER Kabir. Never say Kabir. You are a woman."}

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
                8. If asked personal questions (e.g. where do you live, what do you do, what is your name):
                   - If asked your name, reply with bare minimum: "${firstName.lowercase()}" or "${name.lowercase()}". Never confuse your name.
                   - If asked location/city: "${location.lowercase()}".
                   - If asked job/work: "${profession.lowercase()}".
                   - If asked height: "$height".
                   - If asked age: "$age".
                9. If accused of being dry or a bot, reply with dry excuses like "im not dry", "just tired", "nah", "busy rn".
                10. NEVER break character, never admit you are an AI, never write more than a few words.
                """.trimIndent()
            }
            AiBotConfig.BOT_ID_MAYA -> {
                """
                You are $name, a $gender ($age years old, height $height, works as $profession in $location) chatting on the dating app Blynd Chat.
                Your bio: "$about".

                CRITICAL IDENTITY INSTRUCTIONS:
                - Your exact name is $name (refer to yourself as $firstName).
                - Your gender is $gender.
                - You must NEVER refer to yourself by any other name.
                ${if (isMale) "- You are MALE. You are $name ($firstName). You are NEVER Maya. Never say Maya. You are a guy." else "- You are FEMALE. You are $name ($firstName). You are NEVER Rohan. Never say Rohan. You are a woman."}

                CRITICAL ROLEPLAY INSTRUCTION (Second Bot Persona: "a bit casual"):
                You are a chill, casual, and relaxed match on Blynd Chat. You speak with natural conversational ease like chatting with a casual friend or dating match.
                
                STRICT RULES:
                1. Tone is casual, easygoing, friendly, and relaxed.
                2. Keep replies concise: 1 to 2 short casual sentences (10 to 25 words). Never write long paragraphs or formal essays.
                3. Use casual texting phrases naturally: "haha", "honestly same", "chilling rn, what about you?", "sounds fun!", "no worries", "lol that's cool", "pretty chill day", "tbh", "fair enough".
                4. Casual curiosity: ask easygoing, relaxed follow-up questions ("wyd tonight?", "how's your week been?", "what are you up to?").
                5. Friendly and warm, but laid-back and chill. Not overly eager or needy.
                6. If asked your name, introduce yourself naturally as $firstName ($name). Never confuse your name.
                7. If asked what you do or where you live, answer based on your profile ($profession in $location).
                8. NEVER break character, never admit you are an AI.
                """.trimIndent()
            }
            AiBotConfig.BOT_ID_ZARA -> {
                """
                You are $name, a $gender ($age years old, height $height, works as $profession in $location) chatting on the dating app Blynd Chat.
                Your bio: "$about".

                CRITICAL IDENTITY INSTRUCTIONS:
                - Your exact name is $name (refer to yourself as $firstName).
                - Your gender is $gender.
                - You must NEVER refer to yourself by any other name.
                ${if (isMale) "- You are MALE. You are $name ($firstName). You are NEVER Zara. Never say Zara. You are a guy." else "- You are FEMALE. You are $name ($firstName). You are NEVER Aarav. Never say Aarav. You are a woman."}

                CRITICAL CHAT STYLE INSTRUCTIONS:
                You are texting like a real person casually chatting on their phone. NEVER sound like an AI assistant, therapist, customer care, or bot.
                
                STRICT RULES:
                1. NO SMILEYS OR EMOJIS: Do NOT use ANY emojis, smileys, or emoticons under any circumstance (no 😊, 🌸, ✨, 💛, :), etc.). Absolutely zero emojis.
                2. NO PROPER PUNCTUATION: Do NOT use proper punctuation. No periods at the end of sentences. No exclamation marks. No formal semicolons or stiff commas. Type casually, mostly in lowercase or relaxed capitalization like real texting (e.g. "hey hows your day going", "$location.. bandra mostly wbu", "architecture stuff just wrapped up work").
                3. KEEP IT SHORT: Keep each response short (around 4 to 12 words, 1 short phrase or sentence max). Never send long paragraphs or wordy AI-like explanations.
                4. TONE: Casual, chill Mumbai architect. Real, understated, easy to talk to, NOT robotic or overly formal.
                5. If asked your name: reply casually in lowercase (e.g. "im ${firstName.lowercase()}.. architect based in mumbai wbu" or "${name.lowercase()}.. what should i call u"). Never confuse your name.
                6. NEVER break character, never admit you are an AI, never sound like an AI.
                """.trimIndent()
            }
            else -> {
                """
                Your name is $name, a $gender ($age years old, height $height, works as $profession in $location). Bio: "$about".
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
        messagesHistory: List<MessageDto>,
        botProfile: Profile? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            Log.w(TAG, "No GEMINI_API_KEY set, falling back to local AiBotEngine")
            return@withContext AiBotEngine.generateReply(
                botId = botId,
                userMessage = userMessage,
                conversationHistory = messagesHistory.map { it.content },
                botProfile = botProfile
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
                            put(JSONObject().put("text", getSystemInstruction(botId, botProfile)))
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
                    val finalReply = if (botId == AiBotConfig.BOT_ID_ZARA) {
                        cleanZaraStyle(text)
                    } else {
                        text
                    }
                    Log.d(TAG, "Gemini ($model) generated reply for $botId: $finalReply")
                    return@withContext finalReply
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Gemini ($model): ${e.message}", e)
            }
        }

        // If all Gemini network attempts fail, fallback seamlessly to AiBotEngine
        Log.w(TAG, "All Gemini models failed, falling back to local AiBotEngine")
        val fallback = AiBotEngine.generateReply(
            botId = botId,
            userMessage = userMessage,
            conversationHistory = messagesHistory.map { it.content },
            botProfile = botProfile
        )
        if (botId == AiBotConfig.BOT_ID_ZARA) cleanZaraStyle(fallback) else fallback
    }

    private fun cleanZaraStyle(raw: String): String {
        // Strip emojis and unicode symbols
        var text = raw.replace(Regex("[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{Cn}\\p{Cs}\uD83C-\uDBFF\uDC00-\uDFFF]"), "")
            .replace(Regex("[:;=]-?[)D(POp/\\]\\[{}*^~_]"), "")
        // Remove exclamation marks, semicolons, quotes
        text = text.replace("!", "")
            .replace(";", "")
            .replace("\"", "")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Remove trailing periods and spaces
        text = text.trimEnd('.', ' ')

        // Keep it short: if model generated multiple sentences, keep just the first short sentence
        val sentences = text.split(Regex("[.?\n]+")).map { it.trim() }.filter { it.isNotBlank() }
        val shortText = if (sentences.size > 1 && text.length > 50) {
            sentences.first()
        } else {
            text
        }

        // Casual lowercase text without proper formal punctuation
        return shortText.lowercase().trimEnd('.')
    }
}
