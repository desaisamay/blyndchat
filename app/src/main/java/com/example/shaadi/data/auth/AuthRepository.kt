package com.example.shaadi.data.auth

import android.content.Context
import at.favre.lib.crypto.bcrypt.BCrypt

interface AuthRepository {
    fun isRegistered(): Boolean
    fun register(email: String, password: String): Result<Unit>
    fun login(email: String, password: String): Result<Unit>
    fun logout()
    fun currentUserEmail(): String?
}

class LocalAuthRepository private constructor(private val store: CredentialStore) : AuthRepository {
    override fun isRegistered(): Boolean = store.getEmail() != null && store.getPasswordHash() != null

    override fun register(email: String, password: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.length >= 6) { "Password must be at least 6 characters" }
        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        store.saveCredentials(email.trim(), hash)
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        val savedEmail = store.getEmail() ?: error("No account found. Please sign up.")
        val savedHash = store.getPasswordHash() ?: error("No account found. Please sign up.")
        require(email.trim() == savedEmail) { "Invalid email or password" }
        val result = BCrypt.verifyer().verify(password.toCharArray(), savedHash)
        require(result.verified) { "Invalid email or password" }
    }

    override fun logout() {
        // For local auth demo, clear credentials to force re-signup if needed
        // Alternatively, keep credentials and manage a separate session flag.
    }

    override fun currentUserEmail(): String? = store.getEmail()

    companion object {
        @Volatile private var INSTANCE: LocalAuthRepository? = null

        fun getInstance(context: Context): LocalAuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalAuthRepository(CredentialStore(context.applicationContext)).also { INSTANCE = it }
            }
    }
}
