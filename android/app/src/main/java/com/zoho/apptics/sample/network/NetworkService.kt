package com.zoho.apptics.sample.network

import com.zoho.apptics.analytics.TrackAPIWith
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET


interface NetworkService {

    // https://www.zoho.com/apptics/resources/SDK/android-api_tracking.html

    @TrackAPIWith(2128551619179L)
    @GET("current.json?q=53.1%2C-0.13")
    suspend fun checkFor404(): Response<ResponseBody>

    @TrackAPIWith(2077262005812L)
    @GET("movielist.json")
    suspend fun getMoviesList(): Response<ResponseBody>

    @TrackAPIWith(2079961640855L)
    @GET("/api/?results=25")
    suspend fun getProduct(): Response<ResponseBody>

}