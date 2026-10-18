package com.blyndchat.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class CredentialStore(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(email: String, passwordHash: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD_HASH, passwordHash)
            .apply()
    }

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getPasswordHash(): String? = prefs.getString(KEY_PASSWORD_HASH, null)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun clearToken() { prefs.edit().remove(KEY_TOKEN).apply() }

    fun saveRefreshToken(refreshToken: String?) {
        if (refreshToken.isNullOrBlank()) {
            prefs.edit().remove(KEY_REFRESH_TOKEN).apply()
        } else {
            prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        }
    }
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun savePendingProfile(
        name: String?,
        age: Int?,
        gender: String?,
        avatarUri: String?,
        bio: String? = null,
        lookingFor: String? = null,
        preferredAgeRange: String? = null,
        preferredTopics: String? = null,
        personalityType: String? = null,
        height: String? = null,
        profession: String? = null,
        conversationIntensity: String? = null,
        avoidedTopics: String? = null
    ) {
        val editor = prefs.edit()
        if (name != null) editor.putString(KEY_PENDING_NAME, name) else editor.remove(KEY_PENDING_NAME)
        if (age != null) editor.putString(KEY_PENDING_AGE, age.toString()) else editor.remove(KEY_PENDING_AGE)
        if (gender != null) editor.putString(KEY_PENDING_GENDER, gender) else editor.remove(KEY_PENDING_GENDER)
        if (avatarUri != null) editor.putString(KEY_PENDING_IMAGE_URI, avatarUri) else editor.remove(KEY_PENDING_IMAGE_URI)
        if (bio != null) editor.putString(KEY_PENDING_BIO, bio) else editor.remove(KEY_PENDING_BIO)
        if (lookingFor != null) editor.putString(KEY_PENDING_LOOKING_FOR, lookingFor) else editor.remove(KEY_PENDING_LOOKING_FOR)
        if (preferredAgeRange != null) editor.putString(KEY_PENDING_PREFERRED_AGE_RANGE, preferredAgeRange) else editor.remove(KEY_PENDING_PREFERRED_AGE_RANGE)
        if (preferredTopics != null) editor.putString(KEY_PENDING_PREFERRED_TOPICS, preferredTopics) else editor.remove(KEY_PENDING_PREFERRED_TOPICS)
        if (personalityType != null) editor.putString(KEY_PENDING_PERSONALITY_TYPE, personalityType) else editor.remove(KEY_PENDING_PERSONALITY_TYPE)
        if (height != null) editor.putString(KEY_PENDING_HEIGHT, height) else editor.remove(KEY_PENDING_HEIGHT)
        if (profession != null) editor.putString(KEY_PENDING_PROFESSION, profession) else editor.remove(KEY_PENDING_PROFESSION)
        if (conversationIntensity != null) editor.putString(KEY_PENDING_CONVERSATION_INTENSITY, conversationIntensity) else editor.remove(KEY_PENDING_CONVERSATION_INTENSITY)
        if (avoidedTopics != null) editor.putString(KEY_PENDING_AVOIDED_TOPICS, avoidedTopics) else editor.remove(KEY_PENDING_AVOIDED_TOPICS)
        editor.apply()
    }

    fun getPendingName(): String? = prefs.getString(KEY_PENDING_NAME, null)
    fun getPendingAge(): Int? = prefs.getString(KEY_PENDING_AGE, null)?.toIntOrNull()
    fun getPendingGender(): String? = prefs.getString(KEY_PENDING_GENDER, null)
    fun getPendingBio(): String? = prefs.getString(KEY_PENDING_BIO, null)
    fun getPendingLookingFor(): String? = prefs.getString(KEY_PENDING_LOOKING_FOR, null)
    fun getPendingPreferredAgeRange(): String? = prefs.getString(KEY_PENDING_PREFERRED_AGE_RANGE, null)
    fun getPendingPreferredTopics(): String? = prefs.getString(KEY_PENDING_PREFERRED_TOPICS, null)
    fun getPendingPersonalityType(): String? = prefs.getString(KEY_PENDING_PERSONALITY_TYPE, null)
    fun getPendingHeight(): String? = prefs.getString(KEY_PENDING_HEIGHT, null)
    fun getPendingProfession(): String? = prefs.getString(KEY_PENDING_PROFESSION, null)
    fun getPendingConversationIntensity(): String? = prefs.getString(KEY_PENDING_CONVERSATION_INTENSITY, null)
    fun getPendingAvoidedTopics(): String? = prefs.getString(KEY_PENDING_AVOIDED_TOPICS, null)

    fun clearPendingProfile() {
        prefs.edit()
            .remove(KEY_PENDING_NAME)
            .remove(KEY_PENDING_AGE)
            .remove(KEY_PENDING_GENDER)
            .remove(KEY_PENDING_IMAGE_URI)
            .remove(KEY_PENDING_BIO)
            .remove(KEY_PENDING_LOOKING_FOR)
            .remove(KEY_PENDING_PREFERRED_AGE_RANGE)
            .remove(KEY_PENDING_PREFERRED_TOPICS)
            .remove(KEY_PENDING_PERSONALITY_TYPE)
            .remove(KEY_PENDING_HEIGHT)
            .remove(KEY_PENDING_PROFESSION)
            .remove(KEY_PENDING_CONVERSATION_INTENSITY)
            .remove(KEY_PENDING_AVOIDED_TOPICS)
            .apply()
    }

    fun savePendingImageUri(uri: String?) {
        if (uri == null) {
            prefs.edit().remove(KEY_PENDING_IMAGE_URI).apply()
        } else {
            prefs.edit().putString(KEY_PENDING_IMAGE_URI, uri).apply()
        }
    }
    fun getPendingImageUri(): String? = prefs.getString(KEY_PENDING_IMAGE_URI, null)
    fun clearPendingImageUri() { prefs.edit().remove(KEY_PENDING_IMAGE_URI).apply() }

    fun saveUserId(userId: String?) {
        if (userId.isNullOrBlank()) {
            prefs.edit().remove(KEY_USER_ID).apply()
        } else {
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        }
    }
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    // Theme preference: "black" or "white"
    fun saveThemeMode(mode: String) {
        val normalized = if (mode.equals("black", ignoreCase = true)) "black" else "white"
        prefs.edit().putString(KEY_THEME_MODE, normalized).apply()
    }
    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, null) ?: "black"

    companion object {
        private const val PREF_NAME = "secure_credentials"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_PENDING_IMAGE_URI = "pending_image_uri"
        private const val KEY_PENDING_NAME = "pending_name"
        private const val KEY_PENDING_AGE = "pending_age"
        private const val KEY_PENDING_GENDER = "pending_gender"
        private const val KEY_PENDING_BIO = "pending_bio"
        private const val KEY_PENDING_LOOKING_FOR = "pending_looking_for"
        private const val KEY_PENDING_PREFERRED_AGE_RANGE = "pending_preferred_age_range"
        private const val KEY_PENDING_PREFERRED_TOPICS = "pending_preferred_topics"
        private const val KEY_PENDING_PERSONALITY_TYPE = "pending_personality_type"
        private const val KEY_PENDING_HEIGHT = "pending_height"
        private const val KEY_PENDING_PROFESSION = "pending_profession"
        private const val KEY_PENDING_CONVERSATION_INTENSITY = "pending_conversation_intensity"
        private const val KEY_PENDING_AVOIDED_TOPICS = "pending_avoided_topics"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
