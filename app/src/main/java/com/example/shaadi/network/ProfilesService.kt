package com.example.shaadi.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

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
    @Json(name = "phone_number") val phoneNumber: String?
)

interface ProfilesService {
    // PostgREST: select all columns, optionally limit
    @Headers("Accept: application/json")
    @GET("profiles")
    suspend fun getProfiles(
        @Query("select") select: String = "*",
        @Query("limit") limit: Int? = null
    ): List<ProfileDto>
}
