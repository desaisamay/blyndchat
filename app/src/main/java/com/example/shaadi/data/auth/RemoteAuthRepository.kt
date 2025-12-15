package com.example.shaadi.data.auth

import android.content.Context
import com.example.shaadi.BuildConfig
import com.example.shaadi.network.ApiClient
import com.example.shaadi.network.AuthService
import com.example.shaadi.network.LoginRequest
import com.example.shaadi.network.SignupRequest

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
