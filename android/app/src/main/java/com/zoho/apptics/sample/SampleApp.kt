package com.zoho.apptics.sample

import android.app.Application
import com.zoho.apptics.common.Apptics
import com.zoho.apptics.common.AppticsSettings
import com.zoho.apptics.feedback.AppticsFeedback

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Apptics.init(this)
        AppticsFeedback.enableShakeForFeedback()
        AppticsSettings.setPopupThemeRes(R.style.AppticsAlertTheme)
    }


}
