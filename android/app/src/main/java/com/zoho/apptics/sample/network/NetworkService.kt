package com.zoho.apptics.sample.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * The modern, recommended way to track APIs: there is nothing to annotate here.
 * Every request made through a client that has [com.zoho.apptics.analytics.AppticsApiTrackingInterceptor]
 * installed (see [RetrofitClient]) is tracked automatically — success rate, response time and
 * status code all flow to the Apptics console with no per-endpoint registration.
 *
 * The deprecated @TrackAPIWith(apiId) annotation is no longer required and has been removed.
 *
 * Docs: see refer/api-tracking.md
 */
interface NetworkService {

    // Returns 200 — exercises the happy path (success rate + latency).
    @GET("movielist.json")
    suspend fun getMoviesList(): Response<ResponseBody>

    // Returns 404 — shows that non-2xx responses are tracked with their real status code.
    @GET("current.json?q=53.1%2C-0.13")
    suspend fun checkFor404(): Response<ResponseBody>

    // Query string is stripped before the path is recorded.
    @GET("/api/?results=25")
    suspend fun getProduct(): Response<ResponseBody>

    // A dynamic numeric segment so endpoint normalization is observable live:
    // GET /posts/1 is recorded as /posts/* (and /posts/1/comments as /posts/*/comments).
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<ResponseBody>

    @GET("posts/{id}/comments")
    suspend fun getPostComments(@Path("id") id: Int): Response<ResponseBody>
}
