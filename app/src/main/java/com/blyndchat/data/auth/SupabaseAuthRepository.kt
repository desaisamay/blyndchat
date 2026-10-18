package com.blyndchat.data.auth

import android.content.Context
import com.blyndchat.BuildConfig
import com.blyndchat.network.SupabaseApiClient
import com.blyndchat.network.SupabaseAuthService
import com.blyndchat.network.SupabaseSignupRequest
import com.blyndchat.network.SupabaseTokenRequest

class SupabaseAuthRepository private constructor(
    private val store: CredentialStore,
    private val service: SupabaseAuthService
) : AuthRepository {

    override fun isRegistered(): Boolean = store.getToken() != null

    override fun register(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        try {
            kotlinx.coroutines.runBlocking {
                service.signup(SupabaseSignupRequest(email, password))
            }
        } catch (e: Throwable) {
            throw Exception(extractErrorMessage(e), e)
        }
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        val resp = try {
            kotlinx.coroutines.runBlocking {
                service.passwordGrant(body = SupabaseTokenRequest(email, password))
            }
        } catch (e: Throwable) {
            throw Exception(extractErrorMessage(e), e)
        }
        store.saveToken(resp.accessToken)
        store.saveRefreshToken(resp.refreshToken)
        store.saveCredentials(email, "")

        // Best-effort: fetch and cache user id for downstream flows
        runCatching {
            val authed = com.blyndchat.network.SupabaseApiClient.authedRetrofit(resp.accessToken)
            val authedSvc = authed.create(com.blyndchat.network.SupabaseAuthService::class.java)
            val user = kotlinx.coroutines.runBlocking { authedSvc.getUser() }
            if (!user.id.isNullOrBlank()) {
                store.saveUserId(user.id!!)
            }
        }
    }

    override fun logout() { store.clearToken() }

    override fun currentUserEmail(): String? = store.getEmail()

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
