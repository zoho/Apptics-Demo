package com.zoho.apptics.sample.network.multidomain

import okhttp3.Request

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