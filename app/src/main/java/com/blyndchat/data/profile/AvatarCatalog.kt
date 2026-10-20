package com.blyndchat.data.profile

/**
 * Catalog for gender-specific AI avatar photos.
 * Provides 10 AI male avatars and 10 AI female avatars.
 */
object AvatarCatalog {
    val maleAvatars: List<String> = listOf(
        "male_1.png",
        "male_2.png",
        "male_3.png",
        "male_4.png",
        "male_5.png",
        "male_6.png",
        "male_7.png",
        "male_8.png",
        "male_9.png",
        "male_10.png"
    )

    val femaleAvatars: List<String> = listOf(
        "female_1.png",
        "female_2.png",
        "female_3.png",
        "female_4.png",
        "female_5.png",
        "female_6.png",
        "female_7.png",
        "female_8.png",
        "female_9.png",
        "female_10.png"
    )

    fun getAvatarsForGender(gender: String?): List<String> {
        return when {
            gender.equals("Male", ignoreCase = true) -> maleAvatars
            gender.equals("Female", ignoreCase = true) -> femaleAvatars
            else -> femaleAvatars + maleAvatars
        }
    }

    fun defaultAvatarForGender(gender: String?): String {
        return if (gender.equals("Male", ignoreCase = true)) {
            "male_1.png"
        } else {
            "female_1.png"
        }
    }

    fun isMaleAvatar(filename: String?): Boolean {
        if (filename == null) return false
        val clean = filename.substringAfterLast('/')
        return maleAvatars.contains(clean)
    }

    fun isFemaleAvatar(filename: String?): Boolean {
        if (filename == null) return false
        val clean = filename.substringAfterLast('/')
        return femaleAvatars.contains(clean)
    }

    fun getAssetUri(filename: String): String {
        val clean = filename.substringAfterLast('/')
        return "file:///android_asset/avatars/$clean"
    }
}
