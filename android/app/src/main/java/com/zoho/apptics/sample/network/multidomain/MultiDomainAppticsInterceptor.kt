package com.zoho.apptics.sample.network.multidomain

import com.zoho.apptics.analytics.AppticsApiTracker
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that times each request and reports it to Apptics' API
 * tracking module. Each (url, method) pair maps to a numeric tracking ID
 * configured on the Apptics console — see [TrackIdHandler].
 *
 * Docs: https://www.zoho.com/apptics/resources/SDK/android-api_tracking.html
 */
class MultiDomainAppticsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val appticsAPITrackId = TrackIdHandler.getAPITrackID(request)
        return if (appticsAPITrackId == 0L) {
            chain.proceed(request)
        } else {
            // startTrackApi opens a timing window keyed by the tracking ID and
            // returns a per-call trackID; endTrackApi closes it with the
            // response code so latency + status appear in the Apptics console.
            // Docs: https://www.zoho.com/apptics/resources/SDK/android-api_tracking.html
            val trackID = AppticsApiTracker.startTrackApi(appticsAPITrackId, request.method)
            val response = chain.proceed(request)
            AppticsApiTracker.endTrackApi(trackID, response.code)
            response
        }
    }

}