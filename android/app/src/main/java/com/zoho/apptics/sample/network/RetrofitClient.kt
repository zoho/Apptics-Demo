package com.zoho.apptics.sample.network

import com.zoho.apptics.analytics.AppticsApiTrackingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    const val BASE_URL_ONE = "https://howtodoandroid.com/apis/"
    const val BASE_URL_TWO = "https://randomuser.me"

    // Used for the endpoint-normalization demo: /posts/{id} -> /posts/*
    const val BASE_URL_THREE = "https://jsonplaceholder.typicode.com/"

    private fun getRetrofit(baseUrl : String): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.apply {
            addInterceptor(logger)
            // The only line needed for automatic API tracking. Every request through
            // this client is timed and reported to Apptics; filtering and path
            // normalization are controlled by AppticsApiTracker.configure {} (see MyApp).
            // Docs: see refer/api-tracking.md
            addInterceptor(AppticsApiTrackingInterceptor())
            readTimeout(120, TimeUnit.SECONDS)
            writeTimeout(120, TimeUnit.SECONDS)
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .build()
    }

    fun getApiService(baseUrl : String): NetworkService {
        return getRetrofit(baseUrl).create(NetworkService::class.java)
    }

}