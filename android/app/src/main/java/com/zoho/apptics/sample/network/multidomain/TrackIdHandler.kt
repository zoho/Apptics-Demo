package com.zoho.apptics.sample.network.multidomain

import okhttp3.Request

/**
 * DEPRECATED — backward-compatibility example only.
 *
 * Maps each (domain, url) to a numeric tracking ID that used to be registered on the
 * Apptics web console. The modern SDK needs none of this: install
 * AppticsApiTrackingInterceptor and, if you have regional domains, merge them with
 * `AppticsApiTracker.configure { groupDomains("api.myapp.*") }`.
 *
 * Docs: see refer/api-tracking.md
 */
object TrackIdHandler {

    // Replace your Apptics Tracking ID
    private val domain1 = HashMap<String, Long>().apply {
        put("${RetrofitClientMD.BASE_URL_ONE}/api/result", 2077262005812L)
        put("${RetrofitClientMD.BASE_URL_ONE}movielist.json", 2079961640855L)
    }

    // Replace your Apptics Tracking ID
    private val domain2 = HashMap<String, Long>().apply {
        put("${RetrofitClientMD.BASE_URL_TWO}/api/results", 2128921831835L)
        put("${RetrofitClientMD.BASE_URL_TWO}movielist.json", 2129518180443L)
    }

    fun getAPITrackID(request: Request): Long {
        val url = request.url.toString()
        if (url.startsWith(RetrofitClientMD.BASE_URL_ONE)){
            return domain1[url] ?: 0L
        }
        if (url.startsWith(RetrofitClientMD.BASE_URL_TWO)){
            return domain2[url] ?: 0L
        }
        return 0L
    }

}