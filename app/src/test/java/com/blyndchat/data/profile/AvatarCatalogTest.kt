package com.blyndchat.data.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AvatarCatalogTest {

    @Test
    fun maleAvatars_containsExactlyTenPhotos() {
        assertEquals(10, AvatarCatalog.maleAvatars.size)
        assertTrue(AvatarCatalog.maleAvatars.contains("male_1.png"))
        assertTrue(AvatarCatalog.maleAvatars.contains("male_10.png"))
    }

    @Test
    fun femaleAvatars_containsExactlyTenPhotos() {
        assertEquals(10, AvatarCatalog.femaleAvatars.size)
        assertTrue(AvatarCatalog.femaleAvatars.contains("female_1.png"))
        assertTrue(AvatarCatalog.femaleAvatars.contains("female_10.png"))
    }

    @Test
    fun getAvatarsForGender_returnsMalePhotosForMale() {
        val avatars = AvatarCatalog.getAvatarsForGender("Male")
        assertEquals(10, avatars.size)
        assertTrue(avatars.all { it.startsWith("male_") })
    }

    @Test
    fun getAvatarsForGender_returnsFemalePhotosForFemale() {
        val avatars = AvatarCatalog.getAvatarsForGender("Female")
        assertEquals(10, avatars.size)
        assertTrue(avatars.all { it.startsWith("female_") })
    }

    @Test
    fun defaultAvatarForGender_returnsAppropriateDefault() {
        assertEquals("male_1.png", AvatarCatalog.defaultAvatarForGender("Male"))
        assertEquals("female_1.png", AvatarCatalog.defaultAvatarForGender("Female"))
    }

    @Test
    fun getAssetUri_formatsCorrectAndroidAssetUri() {
        assertEquals("file:///android_asset/avatars/male_1.png", AvatarCatalog.getAssetUri("male_1.png"))
        assertEquals("file:///android_asset/avatars/female_5.png", AvatarCatalog.getAssetUri("female_5.png"))
        assertEquals("file:///android_asset/avatars/female_3.png", AvatarCatalog.getAssetUri("file:///android_asset/avatars/female_3.png"))
    }
}
