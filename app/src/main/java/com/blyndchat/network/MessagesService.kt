package com.blyndchat.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class MessageDto(
    val id: String?,
    @Json(name = "conversation_id") val conversationId: String,
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "receiver_id") val receiverId: String,
    val content: String,
    @Json(name = "created_at") val createdAt: String?
)

interface MessagesService {
    @Headers("Accept: application/json")
    @GET("messages")
    suspend fun getMessages(
        @Query("conversation_id") conversationIdEq: String,
        @Query("order") order: String = "created_at.asc"
    ): List<MessageDto>

    @Headers(
        "Accept: application/json",
        "Prefer: return=minimal"
    )
    @POST("messages")
    suspend fun sendMessage(@Body body: MessageDto): Response<Void>
}
