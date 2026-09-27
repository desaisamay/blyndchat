package com.example.shaadi.data.auth

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
        private const val KEY_USER_ID = "user_id"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
