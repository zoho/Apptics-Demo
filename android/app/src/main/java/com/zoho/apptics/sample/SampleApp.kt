package com.zoho.apptics.sample

import android.app.Application
import com.zoho.apptics.common.Apptics

class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Apptics.init(this)
    }
}