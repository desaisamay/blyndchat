package com.blyndchat.data.auth

object SecurityQuestions {
    val list = listOf(
        "What city were you born in?",
        "What was the name of your first school?",
        "What was your childhood nickname?",
        "What is your mother's maiden name?",
        "What was the make of your first car or bike?",
        "What is the name of your favorite pet?",
        "What is your favorite book or movie?"
    )

    val defaultQuestion: String = list[0]

    /**
     * Case-insensitive matching for answer validation.
     */
    fun matches(expectedAnswer: String?, actualAnswer: String?): Boolean {
        if (expectedAnswer.isNullOrBlank() || actualAnswer.isNullOrBlank()) return false
        return expectedAnswer.trim().equals(actualAnswer.trim(), ignoreCase = true)
    }
}
