package com.blyndchat.data.auth

import android.content.Context
import at.favre.lib.crypto.bcrypt.BCrypt

interface AuthRepository {
    fun isRegistered(): Boolean
    fun register(email: String, password: String): Result<Unit>
    fun register(
        email: String,
        password: String,
        securityQuestion: String?,
        securityAnswer: String?
    ): Result<Unit> = register(email, password)
    fun login(email: String, password: String): Result<Unit>
    fun logout()
    fun currentUserEmail(): String?
    fun getSecurityQuestion(email: String): String? = null
    fun validateSecurityAnswer(email: String, answer: String): Boolean = false
    suspend fun resetPasswordWithSecurityAnswer(
        email: String,
        answer: String,
        newPassword: String
    ): Result<Unit> = Result.failure(UnsupportedOperationException("Security question reset not supported"))
    suspend fun sendPasswordResetOtp(email: String): Result<Unit>
    suspend fun resetPasswordWithOtp(email: String, otp: String, newPassword: String): Result<Unit>
}

class LocalAuthRepository private constructor(private val store: CredentialStore) : AuthRepository {
    override fun isRegistered(): Boolean = store.getEmail() != null && store.getPasswordHash() != null

    override fun register(email: String, password: String): Result<Unit> =
        register(email, password, null, null)

    override fun register(
        email: String,
        password: String,
        securityQuestion: String?,
        securityAnswer: String?
    ): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.length >= 6) { "Password must be at least 6 characters" }
        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        store.saveCredentials(email.trim(), hash)
        store.savePasswordForEmail(email.trim(), password)
        if (!securityQuestion.isNullOrBlank() && !securityAnswer.isNullOrBlank()) {
            store.saveSecurityQuestion(email.trim(), securityQuestion, securityAnswer)
        }
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        val savedEmail = store.getEmail() ?: error("No account found. Please sign up.")
        val savedHash = store.getPasswordHashForEmail(email.trim()) ?: store.getPasswordHash() ?: error("No account found. Please sign up.")
        val plainSaved = store.getPasswordForEmail(email.trim())
        if (plainSaved != null && plainSaved == password) {
            return@runCatching
        }
        val result = BCrypt.verifyer().verify(password.toCharArray(), savedHash)
        require(result.verified) { "Invalid email or password" }
    }

    override fun logout() {
        // For local auth demo, clear credentials to force re-signup if needed
        // Alternatively, keep credentials and manage a separate session flag.
    }

    override fun currentUserEmail(): String? = store.getEmail()

    override fun getSecurityQuestion(email: String): String? {
        return store.getSecurityQuestion(email)
    }

    override fun validateSecurityAnswer(email: String, answer: String): Boolean {
        return store.validateSecurityAnswer(email, answer)
    }

    override suspend fun resetPasswordWithSecurityAnswer(
        email: String,
        answer: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(answer.isNotBlank()) { "Secret answer is required" }
        require(newPassword.length >= 6) { "Password must be at least 6 characters" }
        val isValid = store.validateSecurityAnswer(email.trim(), answer)
        require(isValid) { "Incorrect answer to the secret question. Please try again." }
        val hash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        store.saveCredentials(email.trim(), hash)
        store.savePasswordForEmail(email.trim(), newPassword)
    }

    override suspend fun sendPasswordResetOtp(email: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        val savedEmail = store.getEmail()
        if (savedEmail != null && !savedEmail.equals(email.trim(), ignoreCase = true)) {
            error("No account found with this email.")
        }
    }

    override suspend fun resetPasswordWithOtp(email: String, otp: String, newPassword: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(otp.isNotBlank()) { "OTP is required" }
        require(newPassword.length >= 6) { "Password must be at least 6 characters" }
        val hash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        store.saveCredentials(email.trim(), hash)
        store.savePasswordForEmail(email.trim(), newPassword)
    }

    companion object {
        @Volatile private var INSTANCE: LocalAuthRepository? = null

        fun getInstance(context: Context): LocalAuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalAuthRepository(CredentialStore(context.applicationContext)).also { INSTANCE = it }
            }
    }
}
