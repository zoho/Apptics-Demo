package com.zoho.apptics.sample.network.multidomain

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface NetworkServiceMD {


    @GET("movielist.json")
    suspend fun getMoviesList(): Response<ResponseBody>

    @GET("/api/results")
    suspend fun getProduct(): Response<ResponseBody>

}