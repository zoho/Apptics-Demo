package com.zoho.apptics.sample

import android.app.Application
import com.zoho.apptics.analytics.AppticsApiTracker
import com.zoho.apptics.common.Apptics
import com.zoho.apptics.logger.AppticsLogger

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Bootstraps the Apptics SDK — must be called once, in Application.onCreate(),
        // before any other Apptics API.
        // Docs: https://www.zoho.com/apptics/resources/SDK/android-integrations.html
        Apptics.init(this)

        // Turns on remote logging so AppticsLogger.{v,d,i,w,e}() writes are captured
        // and uploaded to the Apptics console.
        // Docs: https://www.zoho.com/apptics/resources/SDK/android-remote_logger.html
        AppticsLogger.enable()

        // Configures the *automatic* API tracking that AppticsApiTrackingInterceptor
        // performs for every OkHttp request (see RetrofitClient). Call once at startup,
        // before any network call. Each configure() call replaces the previous config
        // entirely, so keep everything in a single block.
        // With no configure() call at all, the defaults apply: every request is tracked
        // and dynamic path segments (numeric IDs, UUIDs, JWTs) are normalized to "*".
        // Docs: see refer/api-tracking.md
        AppticsApiTracker.configure {
            // Aggregate regional variants of a host under a single wildcard so
            // api.myapp.com / .in / .ae all report as one logical endpoint.
            groupDomains("api.myapp.*")

            // Segments that look dynamic but are meaningful — never replace with "*".
            preserveSegments("v1", "v2")

            // Health-check / internal noise we don't want cluttering the dashboard.
            ignoreEndpoint("/health", "/internal/**")

            // autoDetection(true) is the default — numeric IDs / UUIDs / JWTs -> "*".
        }
    }
}