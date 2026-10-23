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
        val key = email.trim().lowercase()
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD_HASH, passwordHash)
            .putString("pass_hash_$key", passwordHash)
            .apply()
    }

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getPasswordHash(): String? = prefs.getString(KEY_PASSWORD_HASH, null)
    fun getPasswordHashForEmail(email: String): String? {
        val key = email.trim().lowercase()
        return prefs.getString("pass_hash_$key", null) ?: prefs.getString(KEY_PASSWORD_HASH, null)
    }

    fun saveSecurityQuestion(email: String, question: String, answer: String) {
        val key = email.trim().lowercase()
        prefs.edit()
            .putString("security_q_$key", question)
            .putString("security_a_$key", answer.trim())
            .putString(KEY_SECURITY_QUESTION, question)
            .putString(KEY_SECURITY_ANSWER, answer.trim())
            .apply()
    }

    fun getSecurityQuestion(email: String? = null): String? {
        if (!email.isNullOrBlank()) {
            val key = email.trim().lowercase()
            prefs.getString("security_q_$key", null)?.let { return it }
        }
        return prefs.getString(KEY_SECURITY_QUESTION, null) ?: SecurityQuestions.defaultQuestion
    }

    fun getSecurityAnswer(email: String? = null): String? {
        if (!email.isNullOrBlank()) {
            val key = email.trim().lowercase()
            prefs.getString("security_a_$key", null)?.let { return it }
        }
        return prefs.getString(KEY_SECURITY_ANSWER, null)
    }

    fun validateSecurityAnswer(email: String?, answer: String): Boolean {
        val stored = getSecurityAnswer(email)
        if (stored.isNullOrBlank()) {
            // Fallback for default demo account or biodata
            val a = answer.trim()
            if (a.equals("Bangalore", ignoreCase = true) ||
                a.equals("Bengaluru", ignoreCase = true) ||
                a.equals("Belgaum", ignoreCase = true) ||
                a.equals("Belagavi", ignoreCase = true)) {
                return true
            }
            return false
        }
        return SecurityQuestions.matches(stored, answer)
    }

    fun savePasswordForEmail(email: String, newPassword: String) {
        val key = email.trim().lowercase()
        prefs.edit()
            .putString("plain_pass_$key", newPassword)
            .apply()
    }

    fun getPasswordForEmail(email: String): String? {
        val key = email.trim().lowercase()
        return prefs.getString("plain_pass_$key", null)
    }

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
        avoidedTopics: String? = null,
        location: String? = null,
        religion: String? = null,
        caste: String? = null,
        annualIncome: String? = null,
        phoneNumber: String? = null,
        securityQuestion: String? = null,
        securityAnswer: String? = null
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
        if (location != null) editor.putString(KEY_PENDING_LOCATION, location) else editor.remove(KEY_PENDING_LOCATION)
        if (religion != null) editor.putString(KEY_PENDING_RELIGION, religion) else editor.remove(KEY_PENDING_RELIGION)
        if (caste != null) editor.putString(KEY_PENDING_CASTE, caste) else editor.remove(KEY_PENDING_CASTE)
        if (annualIncome != null) editor.putString(KEY_PENDING_ANNUAL_INCOME, annualIncome) else editor.remove(KEY_PENDING_ANNUAL_INCOME)
        if (phoneNumber != null) editor.putString(KEY_PENDING_PHONE_NUMBER, phoneNumber) else editor.remove(KEY_PENDING_PHONE_NUMBER)
        if (securityQuestion != null) editor.putString(KEY_PENDING_SECURITY_QUESTION, securityQuestion) else editor.remove(KEY_PENDING_SECURITY_QUESTION)
        if (securityAnswer != null) editor.putString(KEY_PENDING_SECURITY_ANSWER, securityAnswer) else editor.remove(KEY_PENDING_SECURITY_ANSWER)
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
    fun getPendingLocation(): String? = prefs.getString(KEY_PENDING_LOCATION, null)
    fun getPendingReligion(): String? = prefs.getString(KEY_PENDING_RELIGION, null)
    fun getPendingCaste(): String? = prefs.getString(KEY_PENDING_CASTE, null)
    fun getPendingAnnualIncome(): String? = prefs.getString(KEY_PENDING_ANNUAL_INCOME, null)
    fun getPendingPhoneNumber(): String? = prefs.getString(KEY_PENDING_PHONE_NUMBER, null)
    fun getPendingSecurityQuestion(): String? = prefs.getString(KEY_PENDING_SECURITY_QUESTION, null)
    fun getPendingSecurityAnswer(): String? = prefs.getString(KEY_PENDING_SECURITY_ANSWER, null)

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
            .remove(KEY_PENDING_LOCATION)
            .remove(KEY_PENDING_RELIGION)
            .remove(KEY_PENDING_CASTE)
            .remove(KEY_PENDING_ANNUAL_INCOME)
            .remove(KEY_PENDING_PHONE_NUMBER)
            .remove(KEY_PENDING_SECURITY_QUESTION)
            .remove(KEY_PENDING_SECURITY_ANSWER)
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

    // User Gender persistence
    fun saveUserGender(gender: String?) {
        if (gender.isNullOrBlank()) {
            prefs.edit().remove(KEY_USER_GENDER).apply()
        } else {
            prefs.edit().putString(KEY_USER_GENDER, gender).apply()
        }
    }
    fun getUserGender(): String? = prefs.getString(KEY_USER_GENDER, null) ?: getPendingGender()

    // 24-hour / 12-hour profile limit timestamp
    fun saveDailyLimitTimestamp(timestamp: Long) {
        prefs.edit().putLong(KEY_DAILY_LIMIT_TIMESTAMP, timestamp).apply()
    }
    fun getDailyLimitTimestamp(): Long = prefs.getLong(KEY_DAILY_LIMIT_TIMESTAMP, 0L)
    fun clearDailyLimitTimestamp() {
        prefs.edit().remove(KEY_DAILY_LIMIT_TIMESTAMP).apply()
    }

    // 12-hour account swipe cooldown
    fun saveSwipeCooldown(userId: String?, timestamp: Long) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        prefs.edit().putLong("swipe_cooldown_$uid", timestamp).apply()
    }

    fun getSwipeCooldown(userId: String?): Long {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        return prefs.getLong("swipe_cooldown_$uid", 0L)
    }

    fun clearSwipeCooldown(userId: String?) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        prefs.edit().remove("swipe_cooldown_$uid").apply()
    }

    // Session swipe counter (25 swipes limit)
    fun getSessionSwipeCount(userId: String?): Int {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        return prefs.getInt("session_swipes_$uid", 0)
    }

    fun incrementSessionSwipeCount(userId: String?): Int {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        val count = getSessionSwipeCount(userId) + 1
        prefs.edit().putInt("session_swipes_$uid", count).apply()
        return count
    }

    fun resetSessionSwipeCount(userId: String?) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        prefs.edit().remove("session_swipes_$uid").apply()
    }

    // Swiped profile IDs for deduplication across sessions
    fun getSwipedProfileIds(userId: String?): Set<String> {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        return prefs.getStringSet("swiped_profiles_$uid", emptySet()) ?: emptySet()
    }

    fun addSwipedProfileId(userId: String?, profileId: String) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        val current = getSwipedProfileIds(userId).toMutableSet()
        current.add(profileId)
        prefs.edit().putStringSet("swiped_profiles_$uid", current).apply()
    }

    fun clearSwipedProfileIds(userId: String?) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "default"
        prefs.edit().remove("swiped_profiles_$uid").apply()
    }

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
        private const val KEY_PENDING_LOCATION = "pending_location"
        private const val KEY_PENDING_RELIGION = "pending_religion"
        private const val KEY_PENDING_CASTE = "pending_caste"
        private const val KEY_PENDING_ANNUAL_INCOME = "pending_annual_income"
        private const val KEY_PENDING_PHONE_NUMBER = "pending_phone_number"
        private const val KEY_PENDING_SECURITY_QUESTION = "pending_security_question"
        private const val KEY_PENDING_SECURITY_ANSWER = "pending_security_answer"
        private const val KEY_SECURITY_QUESTION = "security_question"
        private const val KEY_SECURITY_ANSWER = "security_answer"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_USER_GENDER = "user_gender"
        private const val KEY_DAILY_LIMIT_TIMESTAMP = "daily_limit_timestamp"
    }
}
