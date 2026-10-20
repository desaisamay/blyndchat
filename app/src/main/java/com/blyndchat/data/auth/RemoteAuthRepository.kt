package com.blyndchat.data.auth

import android.content.Context
import com.blyndchat.BuildConfig
import com.blyndchat.network.ApiClient
import com.blyndchat.network.AuthService
import com.blyndchat.network.LoginRequest
import com.blyndchat.network.SignupRequest

class RemoteAuthRepository private constructor(
    private val store: CredentialStore,
    private val service: AuthService
) : AuthRepository {

    override fun isRegistered(): Boolean = store.getToken() != null

    override fun register(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.API_BASE_URL.isNotBlank()) { "API_BASE_URL is not configured" }
        val resp = kotlinx.coroutines.runBlocking {
            service.register(SignupRequest(email, password))
        }
        require(resp.success) { resp.message ?: "Signup failed" }
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.API_BASE_URL.isNotBlank()) { "API_BASE_URL is not configured" }
        val resp = kotlinx.coroutines.runBlocking {
            service.login(LoginRequest(email, password))
        }
        store.saveToken(resp.token)
        store.saveCredentials(email, "") // optional: we don't store password hash when using remote
    }

    override fun logout() {
        store.clearToken()
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
        val hash = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        store.saveCredentials(email.trim(), hash)
        store.savePasswordForEmail(email.trim(), newPassword)
    }

    override suspend fun sendPasswordResetOtp(email: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        // Remote auth stub
    }

    override suspend fun resetPasswordWithOtp(email: String, otp: String, newPassword: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(otp.isNotBlank()) { "OTP is required" }
        require(newPassword.length >= 6) { "Password must be at least 6 characters" }
        // Remote auth stub
    }

    companion object {
        @Volatile private var INSTANCE: RemoteAuthRepository? = null

        fun getInstance(context: Context): RemoteAuthRepository =
            INSTANCE ?: synchronized(this) {
                val retrofit = ApiClient.retrofit
                val service = retrofit.create(AuthService::class.java)
                INSTANCE ?: RemoteAuthRepository(CredentialStore(context.applicationContext), service)
                    .also { INSTANCE = it }
            }
    }
}
