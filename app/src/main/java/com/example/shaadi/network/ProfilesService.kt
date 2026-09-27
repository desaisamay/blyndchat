package com.example.shaadi.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class ProfileDto(
    val id: String?,
    val name: String?,
    val age: Int?,
    val height: String?,
    val religion: String?,
    val caste: String?,
    @Json(name = "profession") val profession: String?,
    val location: String?,
    @Json(name = "image_url") val imageUrl: String?,
    val about: String?,
    val gender: String?,
    @Json(name = "annual_income") val annualIncome: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    @Json(name = "created_at") val createdAt: String? = null
)

interface ProfilesService {
    // PostgREST: select all columns, optionally limit
    @Headers("Accept: application/json")
    @GET("profiles")
    suspend fun getProfiles(
        @Query("select") select: String = "*",
        @Query("limit") limit: Int? = null
    ): List<ProfileDto>

    // Fetch a single profile by id using PostgREST filter (e.g., id=eq.<uid>)
    @Headers("Accept: application/json")
    @GET("profiles")
    suspend fun getProfilesById(
        @Query("id") idEq: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int? = 1
    ): List<ProfileDto>

    @JsonClass(generateAdapter = true)
    data class ProfileUpsertDto(
        val id: String,
        val name: String?,
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

    // Upsert by primary key using PostgREST
    @Headers(
        "Accept: application/json",
        "Prefer: resolution=merge-duplicates",
        "Prefer: return=minimal"
    )
    @POST("profiles?on_conflict=id")
    suspend fun upsertProfiles(
        @Body body: List<ProfileUpsertDto>
    ): Response<Void>
}
