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

    companion object {
        private const val PREF_NAME = "secure_credentials"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_PENDING_IMAGE_URI = "pending_image_uri"
    }
}
