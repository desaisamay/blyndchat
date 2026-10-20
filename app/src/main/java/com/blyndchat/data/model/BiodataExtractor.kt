package com.blyndchat.data.model

/**
 * Extracts available profile information from marriage biodata and user defaults.
 */
data class BiodataDetails(
    val name: String = "Samay Desai",
    val age: Int = 31,
    val gender: String = "Male",
    val height: String = "5'7\"",
    val profession: String = "Software Engineer",
    val company: String = "SAP Labs",
    val location: String = "Bangalore",
    val annualIncome: String = "₹37 Lakhs per annum",
    val religion: String = "Hindu",
    val caste: String = "Lingayat",
    val phoneNumber: String = "8722505080",
    val education: String = "Bachelor of Engineering",
    val placeOfBirth: String = "Hubli, Karnataka",
    val residence: String = "#53, 1st Main Road, Vidyanagar, Hubli",
    val bio: String = "Software Engineer at SAP Labs, Bangalore. Bachelor of Engineering. Passionate about technology, good food, travel, and sci-fi.",
    val lookingFor: String = "Meaningful conversations & authentic connection",
    val preferredAgeRange: String = "24 - 30",
    val preferredTopics: String = "Tech & AI, Music, Travel, Movies & TV, Books",
    val personalityType: String = "Ambivert",
    val conversationIntensity: String = "Deep",
    val avoidedTopics: String = "Politics",
    val imageUrl: String = "file:///android_asset/avatars/female_1.png"
)

object BiodataExtractor {
    val default = BiodataDetails()
}
