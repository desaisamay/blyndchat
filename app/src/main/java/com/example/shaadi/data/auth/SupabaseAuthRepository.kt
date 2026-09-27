package com.example.shaadi.data.auth

import android.content.Context
import com.example.shaadi.BuildConfig
import com.example.shaadi.network.SupabaseApiClient
import com.example.shaadi.network.SupabaseAuthService
import com.example.shaadi.network.SupabaseSignupRequest
import com.example.shaadi.network.SupabaseTokenRequest

class SupabaseAuthRepository private constructor(
    private val store: CredentialStore,
    private val service: SupabaseAuthService
) : AuthRepository {

    override fun isRegistered(): Boolean = store.getToken() != null

    override fun register(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        kotlinx.coroutines.runBlocking {
            service.signup(SupabaseSignupRequest(email, password))
        }
    }

    override fun login(email: String, password: String): Result<Unit> = runCatching {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) { "SUPABASE_ANON_KEY is not configured" }
        val resp = kotlinx.coroutines.runBlocking {
            service.passwordGrant(body = SupabaseTokenRequest(email, password))
        }
        store.saveToken(resp.accessToken)
        store.saveRefreshToken(resp.refreshToken)
        store.saveCredentials(email, "")

        // Best-effort: fetch and cache user id for downstream flows
        runCatching {
            val authed = com.example.shaadi.network.SupabaseApiClient.authedRetrofit(resp.accessToken)
            val authedSvc = authed.create(com.example.shaadi.network.SupabaseAuthService::class.java)
            val user = kotlinx.coroutines.runBlocking { authedSvc.getUser() }
            if (!user.id.isNullOrBlank()) {
                store.saveUserId(user.id!!)
            }
        }
    }

    override fun logout() { store.clearToken() }

    override fun currentUserEmail(): String? = store.getEmail()

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
