package com.example.shaadi.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class SupabaseUserMeta(
    val name: String? = null,
    val age: Int? = null,
    val height: String? = null,
    val religion: String? = null,
    val caste: String? = null,
    val profession: String? = null,
    val location: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    val about: String? = null,
    val gender: String? = null,
    @Json(name = "annual_income") val annualIncome: String? = null,
    @Json(name = "phone_number") val phoneNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseSignupRequest(
    val email: String,
    val password: String,
    val data: SupabaseUserMeta? = null
)

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

    // Get current user (requires Authorization: Bearer <access_token>)
    @JsonClass(generateAdapter = true)
    data class SupabaseUser(
        val id: String?,
        val email: String?,
        @Json(name = "user_metadata") val userMetadata: Map<String, Any>?,
        @Json(name = "raw_user_meta_data") val rawUserMetaData: Map<String, Any>?
    )

    @GET("user")
    suspend fun getUser(): SupabaseUser
}
