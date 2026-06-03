package com.zoho.apptics.sample.network

import com.zoho.apptics.analytics.AppticsApiTrackingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    const val BASE_URL_ONE = "https://howtodoandroid.com/apis/"
    const val BASE_URL_TWO = "https://randomuser.me"

    private fun getRetrofit(baseUrl : String): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.apply {
            addInterceptor(logger)
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