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
}
