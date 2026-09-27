package com.blyndchat.data.model

data class Profile(
    val id: String,
    val name: String,
    val age: Int,
    val height: String,
    val religion: String,
    val caste: String,
    val profession: String,
    val location: String,
    val imageUrl: String,
    val about: String,
    val gender: String? = null,
    val annualIncome: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String? = null
)
