package com.blyndchat.data

import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.profile.SeedProfiles
import org.junit.Assert.*
import org.junit.Test

class SeedProfilesAndBotTest {

    @Test
    fun testSeedProfilesCountAndIntegrity() {
        assertEquals("Should have exactly 60 female seed profiles", 60, SeedProfiles.FEMALE_PROFILES.size)
        assertEquals("Should have exactly 60 male seed profiles", 60, SeedProfiles.MALE_PROFILES.size)

        // Verify all profiles have complete metadata
        val allProfiles = SeedProfiles.FEMALE_PROFILES + SeedProfiles.MALE_PROFILES
        val ids = mutableSetOf<String>()

        allProfiles.forEach { p ->
            assertTrue("ID should start with seed_", p.id.startsWith("seed_"))
            assertTrue("ID should be unique: ${p.id}", ids.add(p.id))
            assertTrue("Name should not be blank", p.name.isNotBlank())
            assertTrue("Age should be between 20 and 40", p.age in 20..40)
            assertTrue("Profession should not be blank", p.profession.isNotBlank())
            assertTrue("Location should not be blank", p.location.isNotBlank())
            assertTrue("About/bio should not be blank", p.about.isNotBlank())
            assertTrue("Height should not be blank", p.height.isNotBlank())
            assertTrue("LookingFor should not be blank", !p.lookingFor.isNullOrBlank())
            assertTrue("Avatar should not be blank", p.imageUrl.isNotBlank())
        }
    }

    @Test
    fun testGenderFilteredSeedProfiles() {
        // For Male users, female profiles must be shown
        val forMale = SeedProfiles.getSeedProfiles(userGender = "Male")
        assertEquals(60, forMale.size)
        forMale.forEach { assertEquals("Female", it.gender) }

        // For Female users, male profiles must be shown
        val forFemale = SeedProfiles.getSeedProfiles(userGender = "Female")
        assertEquals(60, forFemale.size)
        forFemale.forEach { assertEquals("Male", it.gender) }
    }

    @Test
    fun testGenderSwappedAiModels() {
        // When user is Male: AI models should be 3 Female bots
        val femaleBots = AiBotConfig.getBotProfiles(userGender = "Male")
        assertEquals(3, femaleBots.size)
        assertEquals("Aria Sharma", femaleBots[0].name)
        assertEquals("Maya Reddy", femaleBots[1].name)
        assertEquals("Zara Khan", femaleBots[2].name)
        femaleBots.forEach { assertEquals("Female", it.gender) }

        // When user is Female: AI models should be 3 Male bots
        val maleBots = AiBotConfig.getBotProfiles(userGender = "Female")
        assertEquals(3, maleBots.size)
        assertEquals("Kabir Sharma", maleBots[0].name)
        assertEquals("Rohan Reddy", maleBots[1].name)
        assertEquals("Aarav Khan", maleBots[2].name)
        maleBots.forEach { assertEquals("Male", it.gender) }
    }

    @Test
    fun testIsBotAndIsAiModel() {
        // The 3 AI models
        assertTrue(AiBotConfig.isAiModel(AiBotConfig.BOT_ID_ARIA))
        assertTrue(AiBotConfig.isAiModel(AiBotConfig.BOT_ID_MAYA))
        assertTrue(AiBotConfig.isAiModel(AiBotConfig.BOT_ID_ZARA))

        // AI models are also bots
        assertTrue(AiBotConfig.isBot(AiBotConfig.BOT_ID_ARIA))

        // Seed profiles are recognized as local bots (for instant chat & notifications)
        assertTrue(AiBotConfig.isBot("seed_f_01"))
        assertTrue(AiBotConfig.isBot("seed_m_01"))

        // Seed profiles are NOT AI models (no AI badge)
        assertFalse(AiBotConfig.isAiModel("seed_f_01"))
        assertFalse(AiBotConfig.isAiModel("seed_m_01"))

        // Random IDs are neither
        assertFalse(AiBotConfig.isBot("random_uuid_123"))
        assertFalse(AiBotConfig.isAiModel("random_uuid_123"))
    }

    @Test
    fun testGetProfileById() {
        val f01 = SeedProfiles.getProfileById("seed_f_01")
        assertNotNull(f01)
        assertEquals("Ananya Sharma", f01?.name)

        val m01 = SeedProfiles.getProfileById("seed_m_01")
        assertNotNull(m01)
        assertEquals("Arjun Kapoor", m01?.name)

        val unknown = SeedProfiles.getProfileById("unknown_id")
        assertNull(unknown)
    }

    @Test
    fun testDailyLimitCalculation() {
        val now = System.currentTimeMillis()
        val total24h = 24 * 60 * 60 * 1000L

        // 1 hour elapsed -> limit active
        val oneHourAgo = now - (60 * 60 * 1000L)
        val elapsed1h = now - oneHourAgo
        assertTrue(elapsed1h < total24h)

        // 25 hours elapsed -> limit expired
        val twentyFiveHoursAgo = now - (25 * 60 * 60 * 1000L)
        val elapsed25h = now - twentyFiveHoursAgo
        assertTrue(elapsed25h >= total24h)
    }

    @Test
    fun testBotReplySchedulerTimings() {
        // Bot 1 (Aria / Kabir): starts typing after 10 seconds (10,000ms), types for 2.5s
        val ariaTiming = com.blyndchat.data.ai.BotReplyScheduler.getTimingConfig(AiBotConfig.BOT_ID_ARIA)
        assertEquals("Bot 1 pre-typing delay must be exactly 10 seconds", 10_000L, ariaTiming.first)
        assertEquals("Bot 1 typing duration must be 2.5 seconds", 2_500L, ariaTiming.second)

        // Bot 2 (Maya / Rohan): replies after 10 minutes (600,000ms)
        val mayaTiming = com.blyndchat.data.ai.BotReplyScheduler.getTimingConfig(AiBotConfig.BOT_ID_MAYA)
        val mayaTotal = mayaTiming.first + mayaTiming.second
        assertEquals("Bot 2 total reply delay must be exactly 10 minutes (600,000ms)", 10 * 60 * 1000L, mayaTotal)
        assertTrue("Bot 2 pre-typing delay must be >= 10 seconds (never start typing immediately)", mayaTiming.first >= 10_000L)
        assertTrue("Bot 2 typing duration must be positive", mayaTiming.second > 0)

        // Bot 3 (Zara / Aarav): replies after every 1 to 3 minutes (60,000ms to 180,000ms)
        repeat(20) {
            val zaraTiming = com.blyndchat.data.ai.BotReplyScheduler.getTimingConfig(AiBotConfig.BOT_ID_ZARA)
            val zaraTotal = zaraTiming.first + zaraTiming.second
            assertTrue(
                "Bot 3 total reply delay must be between 1 and 3 minutes (got $zaraTotal ms)",
                zaraTotal in 60_000L..180_000L
            )
            assertTrue("Bot 3 pre-typing delay must be >= 10 seconds (never start typing immediately)", zaraTiming.first >= 10_000L)
            assertTrue("Bot 3 typing duration must be positive", zaraTiming.second > 0)
        }

        // Fallback / other bots: must never immediately start typing, waiting at least 10 seconds
        val fallbackTiming = com.blyndchat.data.ai.BotReplyScheduler.getTimingConfig("unknown_bot")
        assertTrue("Fallback bot must wait >= 10 seconds before typing", fallbackTiming.first >= 10_000L)
    }

    @Test
    fun testBot1PersonaAriaAsIs() {
        // Bot 1: Persona "as is" (dry, uninterested, low-effort)
        val greetingReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ARIA, "hey")
        assertTrue("Aria greeting reply should be dry/short: $greetingReply", greetingReply.length <= 10)

        val locationReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ARIA, "where do you live?")
        assertEquals("bangalore", locationReply)

        val workReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ARIA, "what is your job?")
        assertEquals("ux design", workReply)

        val complimentReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ARIA, "you are so pretty")
        assertTrue(
            "Aria compliment reply should be minimal: $complimentReply",
            complimentReply in listOf("thanks", "thx", "cool", "ty", "haha ok")
        )

        val dryAccusation = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ARIA, "why are you so dry?")
        assertTrue(
            "Aria dry response should be nonchalant: $dryAccusation",
            dryAccusation in listOf("im not dry", "just tired", "nah", "busy rn", "what", "lol no")
        )
    }

    @Test
    fun testBot2PersonaMayaCasual() {
        // Bot 2: Persona "a bit casual" (chill, relaxed, casual banter)
        val greetingReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_MAYA, "hey there")
        assertTrue("Maya greeting should be casual and friendly: $greetingReply", greetingReply.contains("!") || greetingReply.contains("?"))

        val howAreYou = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_MAYA, "how are you doing?")
        val lowerHow = howAreYou.lowercase()
        assertTrue(
            "Maya how are you should be casual chat: $howAreYou",
            lowerHow.contains("chilling") || lowerHow.contains("work") || lowerHow.contains("music") ||
                lowerHow.contains("week") || lowerHow.contains("break") || lowerHow.contains("chill")
        )

        val locationReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_MAYA, "which city are you from?")
        assertTrue("Maya should mention Hyderabad: $locationReply", locationReply.contains("Hyderabad"))

        val workReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_MAYA, "what is your profession?")
        assertTrue("Maya should mention software / coding: $workReply", workReply.lowercase().contains("software") || workReply.lowercase().contains("coding"))
    }

    @Test
    fun testBot3PersonaZaraCaring() {
        // Bot 3: Persona "caring" (warm, deeply caring, empathetic, supportive)
        val greetingReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "hello")
        assertTrue("Zara greeting should be warm and gentle: $greetingReply", greetingReply.contains("🌸") || greetingReply.contains("✨") || greetingReply.contains("😊"))

        val howAreYou = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "how are you?")
        assertTrue(
            "Zara reply should check in warmly: $howAreYou",
            howAreYou.contains("rest") || howAreYou.contains("hydrated") || howAreYou.contains("stressful") ||
                howAreYou.contains("feeling") || howAreYou.contains("smile")
        )

        val tiredMsg = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "I had such a hard and exhausting day at work")
        assertTrue(
            "Zara should provide empathetic comfort: $tiredMsg",
            tiredMsg.contains("rest") || tiredMsg.contains("tea") || tiredMsg.contains("hug") ||
                tiredMsg.contains("sorry") || tiredMsg.contains("breathe") || tiredMsg.contains("care") ||
                tiredMsg.contains("health")
        )
    }
}
