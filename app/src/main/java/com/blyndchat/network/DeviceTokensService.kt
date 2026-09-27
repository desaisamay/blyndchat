package com.blyndchat.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class DeviceTokenDto(
    @Json(name = "user_id") val userId: String,
    val token: String,
    @Json(name = "updated_at") val updatedAt: String? = null
)

interface DeviceTokensService {
    @Headers(
        "Accept: application/json",
        // Enable upsert behavior; requires a unique index on (user_id, token)
        "Prefer: resolution=merge-duplicates, return=representation"
    )
    @POST("device_tokens?on_conflict=user_id,token")
    suspend fun upsert(@Body body: List<DeviceTokenDto>): Response<List<DeviceTokenDto>>
}
