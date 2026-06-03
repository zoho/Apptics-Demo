package com.zoho.apptics.sample.network.multidomain

import com.zoho.apptics.analytics.AppticsApiTracker
import okhttp3.Interceptor
import okhttp3.Response

/**
 * DEPRECATED — kept only as a backward-compatibility example.
 *
 * This shows the *old* API-tracking flow: each (url, method) pair was mapped to a
 * numeric tracking ID registered on the Apptics web console (see [TrackIdHandler]) and
 * tracked via the deprecated `startTrackApi(apiId: Long, method)` overload.
 *
 * Prefer the modern approach instead: install [com.zoho.apptics.analytics.AppticsApiTrackingInterceptor]
 * (no-arg) on your OkHttpClient — see [com.zoho.apptics.sample.network.RetrofitClient]. It tracks every
 * request automatically with no console registration, and regional domains can be merged with
 * `AppticsApiTracker.configure { groupDomains("api.myapp.*") }` instead of a per-URL ID map.
 *
 * Docs: see refer/api-tracking.md
 */
class MultiDomainAppticsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val appticsAPITrackId = TrackIdHandler.getAPITrackID(request)
        return if (appticsAPITrackId == 0L) {
            chain.proceed(request)
        } else {
            // Deprecated apiId-based flow: startTrackApi opens a timing window keyed by
            // the console tracking ID and returns a per-call trackID; endTrackApi closes
            // it with the response code. New integrations should use the URL-based
            // startTrackApi(url, method) overload instead (see ApiTrackingScreen).
            @Suppress("DEPRECATION")
            val trackID = AppticsApiTracker.startTrackApi(appticsAPITrackId, request.method)
            val response = chain.proceed(request)
            AppticsApiTracker.endTrackApi(trackID, response.code)
            response
        }
    }

}