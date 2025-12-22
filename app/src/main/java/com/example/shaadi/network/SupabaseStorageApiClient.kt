package com.example.shaadi.network

import com.example.shaadi.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SupabaseStorageApiClient {
    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

    fun authedRetrofit(accessToken: String): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(req)
            })
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("${BuildConfig.SUPABASE_URL}/storage/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }
}
