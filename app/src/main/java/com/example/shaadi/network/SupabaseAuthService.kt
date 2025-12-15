package com.example.shaadi.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class SupabaseSignupRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class SupabaseSignupResponse(val id: String?, val email: String?)

@JsonClass(generateAdapter = true)
data class SupabaseTokenRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class SupabaseTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "refresh_token") val refreshToken: String?
)

interface SupabaseAuthService {
    @POST("signup")
    suspend fun signup(@Body body: SupabaseSignupRequest): SupabaseSignupResponse

    @POST("token")
    suspend fun passwordGrant(
        @Query("grant_type") grantType: String = "password",
        @Body body: SupabaseTokenRequest
    ): SupabaseTokenResponse

    // Phone OTP: request and verify
    @JsonClass(generateAdapter = true)
    data class OtpRequest(
        val phone: String,
        val channel: String = "sms",
        @Json(name = "create_user") val createUser: Boolean = true
    )

    @JsonClass(generateAdapter = true)
    data class VerifyRequest(
        val phone: String,
        val token: String,
        val type: String = "sms"
    )

    @JsonClass(generateAdapter = true)
    data class VerifyResponse(
        @Json(name = "access_token") val accessToken: String?,
        @Json(name = "token_type") val tokenType: String?
    )

    @POST("otp")
    suspend fun requestOtp(@Body body: OtpRequest)

    @POST("verify")
    suspend fun verifyOtp(@Body body: VerifyRequest): VerifyResponse
}
