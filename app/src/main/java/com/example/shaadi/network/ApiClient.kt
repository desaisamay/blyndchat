package com.example.shaadi.network

import com.example.shaadi.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private val moshi: Moshi = Moshi.Builder().build()

    private val logging: Interceptor = HttpLoggingInterceptor().apply {
        (this as HttpLoggingInterceptor).level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit by lazy {
        require(BuildConfig.API_BASE_URL.isNotBlank()) { "API_BASE_URL is not set. Add it to local.properties" }
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
