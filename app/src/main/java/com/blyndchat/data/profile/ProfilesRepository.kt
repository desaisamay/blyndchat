package com.blyndchat.data.profile

import android.content.Context
import com.blyndchat.data.ai.AiBotConfig
import com.blyndchat.data.model.Profile
import com.blyndchat.network.ProfileDto
import com.blyndchat.network.ProfilesService
import com.blyndchat.network.SupabaseRestApiClient

class ProfilesRepository private constructor() {
    private val service: ProfilesService = SupabaseRestApiClient.retrofit.create(ProfilesService::class.java)

    /**
     * Fetches profiles in strict sequence:
     * 1. First 3: Built-in AI models tailored to opposite of [userGender].
     * 2. Real profiles from database filtered to opposite gender and excluding [currentUserId].
     * 3. 60 Curated Indian seed profiles of the opposite gender (optionally [shuffled] after 24h).
     */
    suspend fun fetchProfiles(
        userGender: String? = null,
        currentUserId: String? = null,
        shuffled: Boolean = false,
        limit: Int? = 100
    ): Result<List<Profile>> {
        val bots = AiBotConfig.getBotProfiles(userGender)

        val realProfiles = runCatching {
            val dtos = service.getProfiles(select = "*", limit = limit)
            dtos.map { it.toDomain() }
        }.getOrDefault(emptyList())

        val filteredReal = realProfiles.filter { profile ->
            if (!currentUserId.isNullOrBlank() && profile.id == currentUserId) return@filter false
            // Skip incomplete or half-created test rows from DB
            if (profile.age <= 0 || profile.imageUrl.isBlank() || profile.name.isBlank()) return@filter false
            // Strict opposite gender filtering
            when {
                userGender.equals("Male", ignoreCase = true) ->
                    !profile.gender.equals("Male", ignoreCase = true)
                userGender.equals("Female", ignoreCase = true) ->
                    !profile.gender.equals("Female", ignoreCase = true)
                else ->
                    !profile.gender.equals("Male", ignoreCase = true)
            }
        }

        val seeds = SeedProfiles.getSeedProfiles(userGender, shuffled = shuffled)

        val allProfiles = bots + filteredReal + seeds
        return Result.success(allProfiles)
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
