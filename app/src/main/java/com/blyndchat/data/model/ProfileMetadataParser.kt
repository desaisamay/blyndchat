package com.blyndchat.data.model

import org.json.JSONObject

data class ParsedBioData(
    val bio: String = "",
    val lookingFor: String? = null,
    val preferredAgeRange: String? = null,
    val preferredTopics: String? = null,
    val personalityType: String? = null,
    val conversationIntensity: String? = null,
    val avoidedTopics: String? = null
)

object ProfileMetadataParser {

    fun parse(rawAbout: String?): ParsedBioData {
        if (rawAbout.isNullOrBlank()) {
            return ParsedBioData()
        }
        val trimmed = rawAbout.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return runCatching {
                val json = JSONObject(trimmed)
                ParsedBioData(
                    bio = json.optString("bio", ""),
                    lookingFor = json.optString("looking_for").takeIf { !it.isNullOrBlank() },
                    preferredAgeRange = json.optString("preferred_age_range").takeIf { !it.isNullOrBlank() },
                    preferredTopics = json.optString("preferred_topics").takeIf { !it.isNullOrBlank() },
                    personalityType = json.optString("personality_type").takeIf { !it.isNullOrBlank() },
                    conversationIntensity = json.optString("conversation_intensity").takeIf { !it.isNullOrBlank() },
                    avoidedTopics = json.optString("avoided_topics").takeIf { !it.isNullOrBlank() }
                )
            }.getOrDefault(ParsedBioData(bio = rawAbout))
        }
        return ParsedBioData(bio = rawAbout)
    }

    fun encode(
        bio: String,
        lookingFor: String? = null,
        preferredAgeRange: String? = null,
        preferredTopics: String? = null,
        personalityType: String? = null,
        conversationIntensity: String? = null,
        avoidedTopics: String? = null
    ): String {
        val json = JSONObject()
        json.put("bio", bio)
        if (!lookingFor.isNullOrBlank()) json.put("looking_for", lookingFor)
        if (!preferredAgeRange.isNullOrBlank()) json.put("preferred_age_range", preferredAgeRange)
        if (!preferredTopics.isNullOrBlank()) json.put("preferred_topics", preferredTopics)
        if (!personalityType.isNullOrBlank()) json.put("personality_type", personalityType)
        if (!conversationIntensity.isNullOrBlank()) json.put("conversation_intensity", conversationIntensity)
        if (!avoidedTopics.isNullOrBlank()) json.put("avoided_topics", avoidedTopics)
        return json.toString()
    }
}
