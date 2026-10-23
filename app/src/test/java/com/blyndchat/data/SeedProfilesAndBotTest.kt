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
        // For Male users, female profiles must be shown with female AI generated avatars
        val forMale = SeedProfiles.getSeedProfiles(userGender = "Male")
        assertEquals(60, forMale.size)
        forMale.forEach { profile ->
            assertEquals("Female", profile.gender)
            assertTrue("Female profile must have female avatar photo: ${profile.imageUrl}",
                profile.imageUrl.contains("avatars/female_") && profile.imageUrl.endsWith(".png"))
        }

        // For Female users, male profiles must be shown with male AI generated avatars
        val forFemale = SeedProfiles.getSeedProfiles(userGender = "Female")
        assertEquals(60, forFemale.size)
        forFemale.forEach { profile ->
            assertEquals("Male", profile.gender)
            assertTrue("Male profile must have male avatar photo: ${profile.imageUrl}",
                profile.imageUrl.contains("avatars/male_") && profile.imageUrl.endsWith(".png"))
        }
    }

    @Test
    fun testGenderSwappedAiModels() {
        // When user is Male: AI models should be 3 Female bots with female AI photos
        val femaleBots = AiBotConfig.getBotProfiles(userGender = "Male")
        assertEquals(3, femaleBots.size)
        assertEquals("Aria Sharma", femaleBots[0].name)
        assertEquals("Maya Reddy", femaleBots[1].name)
        assertEquals("Zara Khan", femaleBots[2].name)
        femaleBots.forEach {
            assertEquals("Female", it.gender)
            assertTrue("Female bot must have female avatar: ${it.imageUrl}",
                it.imageUrl.contains("avatars/female_") && it.imageUrl.endsWith(".png"))
        }

        // When user is Female: AI models should be 3 Male bots with male AI photos
        val maleBots = AiBotConfig.getBotProfiles(userGender = "Female")
        assertEquals(3, maleBots.size)
        assertEquals("Kabir Sharma", maleBots[0].name)
        assertEquals("Rohan Reddy", maleBots[1].name)
        assertEquals("Aarav Khan", maleBots[2].name)
        maleBots.forEach {
            assertEquals("Male", it.gender)
            assertTrue("Male bot must have male avatar: ${it.imageUrl}",
                it.imageUrl.contains("avatars/male_") && it.imageUrl.endsWith(".png"))
        }
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
        // Bot 3: Persona "casual human" (short, no smileys/emojis, no formal punctuation)
        val greetingReply = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "hello")
        assertTrue("Zara greeting should be short: $greetingReply", greetingReply.length <= 40)
        assertFalse("Zara greeting should have no emojis: $greetingReply", greetingReply.contains("🌸") || greetingReply.contains("✨") || greetingReply.contains("😊"))

        val howAreYou = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "how are you?")
        assertTrue("Zara reply should be casual and lowercase: $howAreYou", howAreYou == howAreYou.lowercase())

        val tiredMsg = com.blyndchat.data.ai.AiBotEngine.generateReply(AiBotConfig.BOT_ID_ZARA, "I had such a hard and exhausting day at work")
        assertFalse("Zara reply should have no emojis: $tiredMsg", tiredMsg.contains("🌸") || tiredMsg.contains("✨") || tiredMsg.contains("😊"))
    }

    @Test
    fun testMaleBotPersonasNeverUseFemaleNames() {
        // Resolve male profiles as seen by female users
        val kabirProfile = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_ARIA, userGender = "Female")
        val rohanProfile = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_MAYA, userGender = "Female")
        val aaravProfile = AiBotConfig.getBotProfile(AiBotConfig.BOT_ID_ZARA, userGender = "Female")

        assertNotNull(kabirProfile)
        assertEquals("Kabir Sharma", kabirProfile?.name)
        assertNotNull(rohanProfile)
        assertEquals("Rohan Reddy", rohanProfile?.name)
        assertNotNull(aaravProfile)
        assertEquals("Aarav Khan", aaravProfile?.name)

        // 1. Male Bot 1: Kabir Sharma
        val kabirNameReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ARIA, "what's your name?", botProfile = kabirProfile
        )
        assertTrue("Kabir name reply must contain kabir: $kabirNameReply", kabirNameReply.lowercase().contains("kabir"))
        assertFalse("Kabir name reply must NEVER contain aria: $kabirNameReply", kabirNameReply.lowercase().contains("aria"))

        val kabirJobReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ARIA, "what is your work?", botProfile = kabirProfile
        )
        assertEquals("product design", kabirJobReply)

        val kabirHeightReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ARIA, "how tall are you?", botProfile = kabirProfile
        )
        assertEquals("5'11", kabirHeightReply)

        val kabirAgeReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ARIA, "how old are you?", botProfile = kabirProfile
        )
        assertEquals("27", kabirAgeReply)

        // 2. Male Bot 2: Rohan Reddy
        val rohanNameReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_MAYA, "who are you?", botProfile = rohanProfile
        )
        assertTrue("Rohan name reply must contain Rohan: $rohanNameReply", rohanNameReply.contains("Rohan"))
        assertFalse("Rohan name reply must NEVER contain Maya: $rohanNameReply", rohanNameReply.contains("Maya"))

        val rohanHeightReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_MAYA, "what is your height?", botProfile = rohanProfile
        )
        assertTrue("Rohan height must be 5'10\": $rohanHeightReply", rohanHeightReply.contains("5'10\""))

        val rohanAgeReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_MAYA, "how old are you?", botProfile = rohanProfile
        )
        assertTrue("Rohan age must be 26: $rohanAgeReply", rohanAgeReply.contains("26"))

        // 3. Male Bot 3: Aarav Khan
        val aaravNameReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ZARA, "what is your name?", botProfile = aaravProfile
        )
        assertTrue("Aarav name reply must contain aarav: $aaravNameReply", aaravNameReply.lowercase().contains("aarav"))
        assertFalse("Aarav name reply must NEVER contain zara: $aaravNameReply", aaravNameReply.lowercase().contains("zara"))

        val aaravHeightReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ZARA, "how tall are you?", botProfile = aaravProfile
        )
        assertEquals("6'0 wbu", aaravHeightReply)

        val aaravAgeReply = com.blyndchat.data.ai.AiBotEngine.generateReply(
            AiBotConfig.BOT_ID_ZARA, "how old are you?", botProfile = aaravProfile
        )
        assertEquals("28 wbu", aaravAgeReply)
    }

    @Test
    fun testBotInterestStoreWorkflow() {
        // Reset in-memory state for fresh testing
        com.blyndchat.data.ai.BotInterestStore.resetForTesting()

        // 1. Fresh reset: bots start at "none" until scheduled
        assertEquals("Initial pending count is 0 until scheduled", 0, com.blyndchat.data.ai.BotInterestStore.pendingCount())

        // Mark as pending to test acceptance/rejection/blocking workflow
        com.blyndchat.data.ai.BotInterestStore.markAsPendingForTesting(AiBotConfig.BOT_ID_ARIA)
        com.blyndchat.data.ai.BotInterestStore.markAsPendingForTesting(AiBotConfig.BOT_ID_MAYA)
        com.blyndchat.data.ai.BotInterestStore.markAsPendingForTesting(AiBotConfig.BOT_ID_ZARA)

        assertEquals("Should have 3 pending bot requests", 3, com.blyndchat.data.ai.BotInterestStore.pendingCount())
        assertTrue("Aria should be pending", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ARIA))
        assertTrue("Maya should be pending", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_MAYA))
        assertTrue("Zara should be pending", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ZARA))

        // None are accepted yet, so user cannot chat with them in Chats tab yet
        assertFalse("Aria should not be accepted yet", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_ARIA))
        assertFalse("Maya should not be accepted yet", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_MAYA))
        assertFalse("Zara should not be accepted yet", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_ZARA))
        assertTrue("Accepted bot IDs set should be empty", com.blyndchat.data.ai.BotInterestStore.getAcceptedBotIds().isEmpty())

        // Verify pending bots list matches user gender
        val pendingForMale = com.blyndchat.data.ai.BotInterestStore.getPendingBots(userGender = "Male")
        assertEquals(3, pendingForMale.size)
        assertEquals("Aria Sharma", pendingForMale[0].name)
        assertEquals("Maya Reddy", pendingForMale[1].name)
        assertEquals("Zara Khan", pendingForMale[2].name)

        val pendingForFemale = com.blyndchat.data.ai.BotInterestStore.getPendingBots(userGender = "Female")
        assertEquals(3, pendingForFemale.size)
        assertEquals("Kabir Sharma", pendingForFemale[0].name)

        // 2. User accepts Aria's request in the Received section
        com.blyndchat.data.ai.BotInterestStore.acceptRequest(
            context = null,
            botId = AiBotConfig.BOT_ID_ARIA,
            effectiveUid = "test_user_123"
        )

        // Now Aria is accepted, remaining pending count is 2
        assertTrue("Aria should now be accepted", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_ARIA))
        assertFalse("Aria should no longer be pending", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ARIA))
        assertEquals(2, com.blyndchat.data.ai.BotInterestStore.pendingCount())
        assertEquals(setOf(AiBotConfig.BOT_ID_ARIA), com.blyndchat.data.ai.BotInterestStore.getAcceptedBotIds())

        // BotMessageStore should have initialized the conversation container, but bot must NOT initiate chat
        val convoId = com.blyndchat.data.ai.BotMessageStore.conversationId("test_user_123", AiBotConfig.BOT_ID_ARIA)
        assertTrue("Conversation must exist in BotMessageStore after acceptance", com.blyndchat.data.ai.BotMessageStore.hasConversation(convoId))
        val initialMsg = com.blyndchat.data.ai.BotMessageStore.lastMessage(convoId)
        assertNull("AI bot must NOT initiate the chat (no greeting message)", initialMsg)

        // User initiates the chat
        com.blyndchat.data.ai.BotMessageStore.addMessage(
            convoId,
            com.blyndchat.network.MessageDto(
                id = "user_msg_1",
                conversationId = convoId,
                senderId = "test_user_123",
                receiverId = AiBotConfig.BOT_ID_ARIA,
                content = "Hey Aria, how's your day?",
                createdAt = com.blyndchat.data.ai.BotMessageStore.nowTimestamp()
            )
        )
        val userSentMsg = com.blyndchat.data.ai.BotMessageStore.lastMessage(convoId)
        assertNotNull(userSentMsg)
        assertEquals("Hey Aria, how's your day?", userSentMsg?.content)

        // 3. User rejects Maya's request
        com.blyndchat.data.ai.BotInterestStore.rejectRequest(context = null, botId = AiBotConfig.BOT_ID_MAYA)
        assertFalse("Maya should not be accepted", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_MAYA))
        assertFalse("Maya should not be pending", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_MAYA))
        assertEquals(com.blyndchat.data.ai.BotInterestStore.STATUS_REJECTED, com.blyndchat.data.ai.BotInterestStore.getStatus(AiBotConfig.BOT_ID_MAYA))
        assertEquals(1, com.blyndchat.data.ai.BotInterestStore.pendingCount())

        // 4. Reset to pending
        com.blyndchat.data.ai.BotInterestStore.resetToPending(context = null)
        assertEquals(3, com.blyndchat.data.ai.BotInterestStore.pendingCount())
        assertTrue(com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ARIA))
        assertTrue(com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_MAYA))
        assertTrue(com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ZARA))

        // 5. Block peer test
        com.blyndchat.data.ai.BotInterestStore.blockPeer(context = null, peerId = AiBotConfig.BOT_ID_ARIA)
        assertTrue("Aria should be blocked", com.blyndchat.data.ai.BotInterestStore.isBlocked(AiBotConfig.BOT_ID_ARIA))
        assertFalse("Aria should not be accepted when blocked", com.blyndchat.data.ai.BotInterestStore.isAccepted(AiBotConfig.BOT_ID_ARIA))
        assertFalse("Aria should not be pending when blocked", com.blyndchat.data.ai.BotInterestStore.isPending(AiBotConfig.BOT_ID_ARIA))
    }

    @Test
    fun testOutgoingRequestAndDelayedAcceptanceWorkflow() = kotlinx.coroutines.runBlocking {
        com.blyndchat.data.ai.BotInterestStore.resetForTesting()

        val seedId = "seed_f_02" // Diya Patel
        // 1. Initial state: not accepted, not pending
        assertFalse("Seed profile should not be accepted initially", com.blyndchat.data.ai.BotInterestStore.isAccepted(seedId))
        assertFalse("Seed profile should not be outgoing pending initially", com.blyndchat.data.ai.BotInterestStore.isOutgoingPending(seedId))

        // 2. User right swipes / sends outgoing request
        var acceptedCallbackFired = false
        com.blyndchat.data.ai.BotInterestStore.sendOutgoingRequest(
            context = null,
            peerId = seedId,
            peerName = "Diya Patel",
            effectiveUid = "user_test_456",
            delayMs = 100L, // Fast for unit testing
            onAccepted = { acceptedCallbackFired = true }
        )

        // Immediately after sending: status is outgoing pending, NOT accepted
        assertTrue("Seed profile must be in outgoing pending state", com.blyndchat.data.ai.BotInterestStore.isOutgoingPending(seedId))
        assertFalse("Seed profile must NOT be accepted yet", com.blyndchat.data.ai.BotInterestStore.isAccepted(seedId))
        assertFalse("Accepted bot IDs must not contain seed profile while pending", com.blyndchat.data.ai.BotInterestStore.getAcceptedBotIds().contains(seedId))

        // 3. Wait for realistic acceptance delay to complete
        kotlinx.coroutines.delay(250L)

        // After acceptance: status is accepted, present in getAcceptedBotIds
        assertTrue("Callback should have fired", acceptedCallbackFired)
        assertTrue("Seed profile must now be accepted", com.blyndchat.data.ai.BotInterestStore.isAccepted(seedId))
        assertFalse("Seed profile should no longer be outgoing pending", com.blyndchat.data.ai.BotInterestStore.isOutgoingPending(seedId))
        assertTrue("Accepted bot IDs must now include seed profile", com.blyndchat.data.ai.BotInterestStore.getAcceptedBotIds().contains(seedId))

        // BotMessageStore conversation initialized
        val convoId = com.blyndchat.data.ai.BotMessageStore.conversationId("user_test_456", seedId)
        assertTrue("Conversation must be initialized in BotMessageStore after acceptance", com.blyndchat.data.ai.BotMessageStore.hasConversation(convoId))
    }

    @Test
    fun testUnreadSymbolAndConversationSortingWorkflow() {
        val user = "user_test_789"
        val peerA = "peer_aria"
        val peerB = "peer_maya"

        val convoA = com.blyndchat.data.ai.BotMessageStore.conversationId(user, peerA)
        val convoB = com.blyndchat.data.ai.BotMessageStore.conversationId(user, peerB)

        com.blyndchat.data.ai.BotMessageStore.clearForTesting()
        com.blyndchat.data.ai.BotMessageStore.initConversation(convoA, timestamp = 1000L)
        com.blyndchat.data.ai.BotMessageStore.initConversation(convoB, timestamp = 2000L)

        // Initially convoB has higher timestamp (2000 > 1000), neither has unread
        assertFalse("convoA must not be unread initially", com.blyndchat.data.ai.BotMessageStore.isUnread(convoA))
        assertFalse("convoB must not be unread initially", com.blyndchat.data.ai.BotMessageStore.isUnread(convoB))
        assertEquals(0, com.blyndchat.data.ai.BotMessageStore.totalUnreadCount())

        val initialOrder = listOf(convoA, convoB).sortedByDescending {
            com.blyndchat.data.ai.BotMessageStore.getLastActivityTimestamp(it)
        }
        assertEquals(convoB, initialOrder[0])
        assertEquals(convoA, initialOrder[1])

        // Now incoming message is received in convoA from peerA
        val incomingMsg = com.blyndchat.network.MessageDto(
            id = "bot_msg_999",
            conversationId = convoA,
            senderId = peerA,
            receiverId = user,
            content = "Hey there, loved your profile!",
            createdAt = com.blyndchat.data.ai.BotMessageStore.nowTimestamp()
        )
        com.blyndchat.data.ai.BotMessageStore.addMessage(convoA, incomingMsg, isIncoming = true)

        // 1. convoA must now be unread with unreadCount = 1
        assertTrue("convoA must now be unread", com.blyndchat.data.ai.BotMessageStore.isUnread(convoA))
        assertEquals(1, com.blyndchat.data.ai.BotMessageStore.unreadCount(convoA))
        assertEquals(1, com.blyndchat.data.ai.BotMessageStore.totalUnreadCount())

        // 2. convoA timestamp is now updated to current time (> 2000L), so it sorts to the top!
        val updatedOrder = listOf(convoA, convoB).sortedByDescending {
            com.blyndchat.data.ai.BotMessageStore.getLastActivityTimestamp(it)
        }
        assertEquals("convoA must now be on top of conversations list", convoA, updatedOrder[0])
        assertEquals(convoB, updatedOrder[1])

        // 3. User opens chat -> markAsRead
        com.blyndchat.data.ai.BotMessageStore.markAsRead(convoA)
        assertFalse("convoA must no longer be unread after reading", com.blyndchat.data.ai.BotMessageStore.isUnread(convoA))
        assertEquals(0, com.blyndchat.data.ai.BotMessageStore.unreadCount(convoA))
        assertEquals(0, com.blyndchat.data.ai.BotMessageStore.totalUnreadCount())

        // convoA remains on top because of its activity timestamp
        val postReadOrder = listOf(convoA, convoB).sortedByDescending {
            com.blyndchat.data.ai.BotMessageStore.getLastActivityTimestamp(it)
        }
        assertEquals("convoA must remain on top after being read", convoA, postReadOrder[0])
    }
}

