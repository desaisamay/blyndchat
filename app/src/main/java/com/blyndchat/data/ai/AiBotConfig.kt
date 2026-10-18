package com.blyndchat.data.ai

import com.blyndchat.data.model.Profile

/**
 * Configuration for 3 built-in AI bot profiles.
 *
 * Each bot has a deterministic UUID (never collides with real Supabase users),
 * a persona name, and realistic profile details.
 * All interactions with bots are handled locally — no server round-trips.
 */
object AiBotConfig {

    /** Well-known UUIDs for the three AI bots. */
    const val BOT_ID_ARIA   = "00000000-0000-0000-0000-000000000001"
    const val BOT_ID_MAYA   = "00000000-0000-0000-0000-000000000002"
    const val BOT_ID_ZARA   = "00000000-0000-0000-0000-000000000003"

    val ALL_BOT_IDS = setOf(BOT_ID_ARIA, BOT_ID_MAYA, BOT_ID_ZARA)

    fun isBot(userId: String?): Boolean = userId in ALL_BOT_IDS

    /** Full Profile objects for each bot. */
    val BOT_PROFILES: List<Profile> = listOf(
        Profile(
            id = BOT_ID_ARIA,
            name = "Aria Sharma",
            age = 26,
            height = "5'5\"",
            religion = "Hindu",
            caste = "Brahmin",
            profession = "UX Designer",
            location = "Bangalore",
            imageUrl = "file:///android_asset/avatars/Avatar_2.png",
            about = "Creative soul who loves designing beautiful experiences. Chai over coffee, always! 🎨✨",
            gender = "Female",
            annualIncome = "12 LPA",
            phoneNumber = "+91 98765 43210",
            createdAt = "2024-06-15"
        ),
        Profile(
            id = BOT_ID_MAYA,
            name = "Maya Reddy",
            age = 24,
            height = "5'3\"",
            religion = "Hindu",
            caste = "Reddy",
            profession = "Software Engineer",
            location = "Hyderabad",
            imageUrl = "file:///android_asset/avatars/Avatar_3.png",
            about = "Code by day, binge-watch by night. Looking for someone who can keep up with my terrible puns 😄",
            gender = "Female",
            annualIncome = "18 LPA",
            phoneNumber = "+91 87654 32109",
            createdAt = "2024-08-22"
        ),
        Profile(
            id = BOT_ID_ZARA,
            name = "Zara Khan",
            age = 27,
            height = "5'6\"",
            religion = "Muslim",
            caste = "Khan",
            profession = "Architect",
            location = "Mumbai",
            imageUrl = "file:///android_asset/avatars/Avatar_4.png",
            about = "Designing spaces and chasing sunsets. Foodie who can talk for hours about biryani 🌅🏗️",
            gender = "Female",
            annualIncome = "15 LPA",
            phoneNumber = "+91 76543 21098",
            createdAt = "2024-07-10"
        )
    )

    fun getBotProfile(botId: String): Profile? =
        BOT_PROFILES.firstOrNull { it.id == botId }
}
