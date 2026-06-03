package com.zoho.apptics.sample.network.multidomain

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClientMD {
    const val BASE_URL_ONE = "https://howtodoandroid.com/apis/"
    const val BASE_URL_TWO = "https://randomuser.me"


    private fun getRetrofit(url: String): Retrofit {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.apply {
            addInterceptor(logger)
            addInterceptor(MultiDomainAppticsInterceptor())
            readTimeout(120, TimeUnit.SECONDS)
            writeTimeout(120, TimeUnit.SECONDS)
        }
        return Retrofit.Builder().baseUrl(url).client(clientBuilder.build()).build()
    }

    fun getApiService(url:String): NetworkServiceMD {
        return getRetrofit(url).create(NetworkServiceMD::class.java)
    }

}