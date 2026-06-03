package com.zoho.apptics.sample

import android.app.Application
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
    }
}