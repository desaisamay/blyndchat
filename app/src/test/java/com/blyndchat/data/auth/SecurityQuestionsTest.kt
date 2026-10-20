package com.blyndchat.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityQuestionsTest {

    @Test
    fun standardQuestionsList_containsExpectedQuestions() {
        assertTrue(SecurityQuestions.list.isNotEmpty())
        assertTrue(SecurityQuestions.list.contains("What city were you born in?"))
        assertTrue(SecurityQuestions.list.contains("What was the name of your first school?"))
        assertEquals("What city were you born in?", SecurityQuestions.defaultQuestion)
    }

    @Test
    fun matches_caseInsensitiveValidation_exactMatch() {
        assertTrue(SecurityQuestions.matches("Bangalore", "Bangalore"))
    }

    @Test
    fun matches_caseInsensitiveValidation_lowercase() {
        assertTrue(SecurityQuestions.matches("Bangalore", "bangalore"))
    }

    @Test
    fun matches_caseInsensitiveValidation_uppercase() {
        assertTrue(SecurityQuestions.matches("Bangalore", "BANGALORE"))
    }

    @Test
    fun matches_caseInsensitiveValidation_camelCaseAndMixedCase() {
        assertTrue(SecurityQuestions.matches("Bangalore", "bAnGaLoRe"))
        assertTrue(SecurityQuestions.matches("StXavierHighSchool", "stxavierhighschool"))
        assertTrue(SecurityQuestions.matches("stXavierHighSchool", "STXAVIERHIGHSCHOOL"))
    }

    @Test
    fun matches_handlesWhitespaceTrimming() {
        assertTrue(SecurityQuestions.matches("Bangalore", "   bangalore   "))
        assertTrue(SecurityQuestions.matches("  Bangalore  ", "BANGALORE"))
    }

    @Test
    fun matches_returnsFalseOnIncorrectAnswer() {
        assertFalse(SecurityQuestions.matches("Bangalore", "Mumbai"))
        assertFalse(SecurityQuestions.matches("St. Xavier's", "DPS"))
    }

    @Test
    fun matches_returnsFalseOnNullOrBlank() {
        assertFalse(SecurityQuestions.matches(null, "Bangalore"))
        assertFalse(SecurityQuestions.matches("Bangalore", null))
        assertFalse(SecurityQuestions.matches("", "Bangalore"))
        assertFalse(SecurityQuestions.matches("Bangalore", "   "))
    }
}
