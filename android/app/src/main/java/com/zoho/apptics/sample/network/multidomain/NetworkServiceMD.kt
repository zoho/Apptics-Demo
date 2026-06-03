// This whole file is a deprecated backward-compatibility demo; the @TrackAPIWith
// deprecation warning is expected and intentional, so silence it here.
@file:Suppress("DEPRECATION")

package com.zoho.apptics.sample.network.multidomain

import com.zoho.apptics.analytics.TrackAPIWith
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

/**
 * DEPRECATED — backward-compatibility example only.
 *
 * Demonstrates the old `@TrackAPIWith(apiId)` annotation, where each endpoint had to be
 * registered on the Apptics web console to obtain a numeric apiId. The annotation is no
 * longer required: the modern [com.zoho.apptics.sample.network.NetworkService] carries no
 * annotations and is tracked automatically by AppticsApiTrackingInterceptor.
 *
 * Docs: see refer/api-tracking.md
 */
interface NetworkServiceMD {

    // Deprecated annotation kept to show the legacy flow still compiles and tracks.
    // New code: delete the annotation entirely.
    @TrackAPIWith(2079961640855L)
    @GET("movielist.json")
    suspend fun getMoviesList(): Response<ResponseBody>

    @GET("/api/results")
    suspend fun getProduct(): Response<ResponseBody>

}
