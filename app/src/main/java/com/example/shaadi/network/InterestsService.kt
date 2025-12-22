package com.example.shaadi.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class InterestDto(
    val id: String?,
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "receiver_id") val receiverId: String,
    val status: String,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?
)

interface InterestsService {
    @Headers("Accept: application/json")
    @GET("interests")
    suspend fun listMyInterests(
        @Query("select") select: String = "*",
        @Query("or") orFilter: String? = null,
        @Query("order") order: String? = null,
        @Query("receiver_id") receiverId: String? = null,
        @Query("sender_id") senderId: String? = null,
        @Query("status") status: String? = null
    ): List<InterestDto>

    @Headers("Accept: application/json", "Prefer: count=exact")
    @GET("interests")
    suspend fun listMyInterestsResponse(
        @Query("select") select: String = "*",
        @Query("or") orFilter: String? = null,
        @Query("order") order: String? = null,
        @Query("receiver_id") receiverId: String? = null,
        @Query("sender_id") senderId: String? = null,
        @Query("status") status: String? = null
    ): Response<List<InterestDto>>

    @Headers(
        "Accept: application/json",
        "Prefer: return=minimal"
    )
    @POST("interests")
    suspend fun sendInterest(@Body body: InterestDto): Response<Void>

    @Headers(
        "Accept: application/json",
        "Prefer: return=minimal"
    )
    @PATCH("interests")
    suspend fun updateInterest(
        @Query("id") idEq: String,
        @Body body: Map<String, String>
    ): Response<Void>
}
