package com.blyndchat.data.profile

import android.content.Context
import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.model.Profile
import com.blyndchat.network.ProfileDto
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient

class ProfilesRepository private constructor() {
    private val service: ProfilesService = SupabaseRestApiClient.retrofit.create(ProfilesService::class.java)

    suspend fun fetchProfiles(limit: Int? = 100): Result<List<Profile>> {
        val realProfiles = runCatching {
            val dtos = service.getProfiles(select = "*", limit = limit)
            dtos.map { it.toDomain() }
        }.getOrDefault(emptyList())
        return Result.success(AiBotConfig.BOT_PROFILES + realProfiles)
    }

    private fun ProfileDto.toDomain(): Profile {
        val parsed = com.blyndchat.data.model.ProfileMetadataParser.parse(this.about)
        return Profile(
            id = this.id ?: "",
            name = this.name ?: "",
            age = this.age ?: 0,
            height = this.height ?: "",
            religion = this.religion ?: "",
            caste = this.caste ?: "",
            profession = this.profession ?: "",
            location = this.location ?: "",
            imageUrl = this.imageUrl ?: "",
            about = parsed.bio.ifBlank { this.about ?: "" },
            gender = this.gender,
            annualIncome = this.annualIncome,
            phoneNumber = this.phoneNumber,
            createdAt = this.createdAt,
            lookingFor = parsed.lookingFor,
            preferredAgeRange = parsed.preferredAgeRange,
            preferredTopics = parsed.preferredTopics,
            personalityType = parsed.personalityType,
            conversationIntensity = parsed.conversationIntensity,
            avoidedTopics = parsed.avoidedTopics
        )
    }

    companion object {
        @Volatile private var instance: ProfilesRepository? = null
        fun getInstance(context: Context): ProfilesRepository =
            instance ?: synchronized(this) {
                instance ?: ProfilesRepository().also { instance = it }
            }
    }
}
