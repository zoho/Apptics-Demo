package com.zoho.apptics.sample

import android.app.Application
import com.zoho.apptics.common.Apptics
import com.zoho.apptics.common.AppticsUser
import java.util.Locale

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Apptics.init(this)
        configureSampleUser()
    }

    private fun configureSampleUser() {
        val userProperty =
            AppticsUser.AppticsUserPropertyBuilder()
                .setFirstName("Mia")
                .setPlanType("pro")
                .setLanguage(Locale.getDefault().toLanguageTag())
                .build()
        AppticsUser.setUser(userId = "coach-mia", userProperty = userProperty)
    }

}
