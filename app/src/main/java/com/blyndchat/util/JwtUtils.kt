package com.blyndchat.util

import android.util.Base64
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi

object JwtUtils {
    data class JwtPayload(
        @Json(name = "sub") val sub: String?,
        @Json(name = "user_id") val userId: String?
    )

    fun getUserIdFromToken(token: String): String? {
        // JWT format: header.payload.signature (Base64Url encoded)
        val parts = token.split(".")
        if (parts.size < 2) return null
        val payloadB64 = parts[1]
        return try {
            val decoded = Base64.decode(payloadB64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val json = String(decoded, Charsets.UTF_8)
            android.util.Log.d("JwtUtils", "payload json=$json")
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(JwtPayload::class.java)
            adapter.fromJson(json)?.let { it.sub ?: it.userId }
        } catch (e: Exception) {
            android.util.Log.e("JwtUtils", "decode failed: ${e.message}", e)
            null
        }
    }
}
