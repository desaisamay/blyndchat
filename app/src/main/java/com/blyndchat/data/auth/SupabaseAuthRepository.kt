package com.blyndchat.data.auth

import android.content.Context
import com.blyndchat.BuildConfig
import com.blyndchat.network.SupabaseApiClient
import com.blyndchat.network.SupabaseAuthService
import com.blyndchat.network.SupabaseSignupRequest
import com.blyndchat.network.SupabaseTokenRequest
import com.blyndchat.network.SupabaseTokenResponse

class SupabaseAuthRepository private constructor(
    private val store: CredentialStore,
    private val service: SupabaseAuthService
) : AuthRepository {

    override fun isRegistered(): Boolean = store.getToken() != null || store.getEmail() != null

    override fun register(email: String, password: String): Result<Unit> =
        register(email, password, null, null)

    override fun register(
        email: String,
        password: String,
        securityQuestion: String?,
        securityAnswer: String?
    ): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        
        if (!securityQuestion.isNullOrBlank() && !securityAnswer.isNullOrBlank()) {
            store.saveSecurityQuestion(email.trim(), securityQuestion, securityAnswer)
        }
        val hash = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, password.toCharArray())
        store.saveCredentials(email.trim(), hash)
        store.savePasswordForEmail(email.trim(), password)

        try {
            kotlinx.coroutines.runBlocking {
                service.signup(SupabaseSignupRequest(email, password))
            }
        } catch (e: Throwable) {
            // If user already registered in Supabase or network issue, but local profile registered
            val msg = extractErrorMessage(e)
            if (!msg.contains("already registered", ignoreCase = true)) {
                throw Exception(msg, e)
            }
        }
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        
        var loggedIn = false
        var resp: SupabaseTokenResponse? = null
        try {
            resp = kotlinx.coroutines.runBlocking {
                service.passwordGrant(body = SupabaseTokenRequest(email, password))
            }
            loggedIn = true
        } catch (e: Throwable) {
            // Check if password matches locally reset password via Secret Question
            val plainSaved = store.getPasswordForEmail(email.trim())
            val hashSaved = store.getPasswordHashForEmail(email.trim())
            if (plainSaved != null && plainSaved == password) {
                loggedIn = true
            } else if (!hashSaved.isNullOrBlank() && at.favre.lib.crypto.bcrypt.BCrypt.verifyer().verify(password.toCharArray(), hashSaved).verified) {
                loggedIn = true
            } else {
                throw Exception(extractErrorMessage(e), e)
            }
        }

        if (resp != null) {
            store.saveToken(resp.accessToken)
            store.saveRefreshToken(resp.refreshToken)
        }
        store.saveCredentials(email, "")
        store.savePasswordForEmail(email, password)

        // Best-effort: fetch and cache user id for downstream flows
        if (resp != null) {
            runCatching {
                val authed = com.blyndchat.network.SupabaseApiClient.authedRetrofit(resp.accessToken)
                val authedSvc = authed.create(com.blyndchat.network.SupabaseAuthService::class.java)
                val user = kotlinx.coroutines.runBlocking { authedSvc.getUser() }
                if (!user.id.isNullOrBlank()) {
                    store.saveUserId(user.id!!)
                }
            }
        }
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

    override suspend fun sendPasswordResetOtp(email: String): Result<Unit> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        runCatching {
            require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
            require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
            require(email.isNotBlank()) { "Email is required" }
            val resp = service.recoverPassword(com.blyndchat.network.SupabaseRecoverRequest(email.trim()))
            if (!resp.isSuccessful) {
                val errorString = resp.errorBody()?.string()
                throw Exception(parseErrorMessageJson(errorString))
            }
        }
    }

    override suspend fun resetPasswordWithOtp(email: String, otp: String, newPassword: String): Result<Unit> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        runCatching {
            require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
            require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
            require(email.isNotBlank()) { "Email is required" }
            require(otp.isNotBlank()) { "OTP is required" }
            require(newPassword.length >= 6) { "Password must be at least 6 characters" }
            
            val verifyResp = try {
                service.verifyEmailOtp(
                    com.blyndchat.network.SupabaseVerifyEmailOtpRequest(
                        email = email.trim(),
                        token = otp.trim()
                    )
                )
            } catch (e: Throwable) {
                throw Exception(extractErrorMessage(e), e)
            }
            
            val accessToken = verifyResp.accessToken
            if (accessToken.isNullOrBlank()) {
                throw Exception("OTP verification failed. Token could not be validated.")
            }
            
            try {
                val authedRetrofit = SupabaseApiClient.authedRetrofit(accessToken)
                val authedSvc = authedRetrofit.create(SupabaseAuthService::class.java)
                authedSvc.updateUser(com.blyndchat.network.SupabaseUpdateUserRequest(password = newPassword))
                Unit
            } catch (e: Throwable) {
                throw Exception(extractErrorMessage(e), e)
            }
        }
    }

    private fun parseErrorMessageJson(jsonStr: String?): String {
        if (!jsonStr.isNullOrBlank()) {
            val parsed = runCatching {
                val json = org.json.JSONObject(jsonStr)
                json.optString("msg", "")
                    .ifBlank { json.optString("message", "") }
                    .ifBlank { json.optString("error_description", "") }
            }.getOrNull()
            if (!parsed.isNullOrBlank()) return parsed
        }
        return "Request failed. Please try again."
    }

    private fun extractErrorMessage(e: Throwable): String {
        if (e is retrofit2.HttpException) {
            val errorBody = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
            return parseErrorMessageJson(errorBody)
        }
        return e.message ?: "Request failed"
    }

    companion object {
        @Volatile private var INSTANCE: SupabaseAuthRepository? = null
        fun getInstance(context: Context): SupabaseAuthRepository =
            INSTANCE ?: synchronized(this) {
                val retrofit = SupabaseApiClient.retrofit
                val service = retrofit.create(SupabaseAuthService::class.java)
                INSTANCE ?: SupabaseAuthRepository(CredentialStore(context.applicationContext), service)
                    .also { INSTANCE = it }
            }
    }
}
