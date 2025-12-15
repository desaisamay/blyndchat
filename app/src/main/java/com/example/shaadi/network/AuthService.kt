package com.example.shaadi.network

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResponse(val token: String)

@JsonClass(generateAdapter = true)
data class SignupRequest(val email: String, val password: String, val name: String? = null)

@JsonClass(generateAdapter = true)
data class SignupResponse(val success: Boolean, val message: String? = null)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body body: SignupRequest): SignupResponse
}
