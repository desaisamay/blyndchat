package com.example.shaadi.network

import com.example.shaadi.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SupabaseRestApiClient {
    private val authHeaderInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val request = original.newBuilder()
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .build()
        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authHeaderInterceptor)
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("${BuildConfig.SUPABASE_URL}/rest/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    fun authedRetrofit(accessToken: String): Retrofit {
        val authed = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.SUPABASE_URL}/rest/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(authed)
            .build()
    }
}
