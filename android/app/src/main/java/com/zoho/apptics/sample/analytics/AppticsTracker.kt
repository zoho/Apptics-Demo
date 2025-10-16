package com.zoho.apptics.sample.analytics

import com.zoho.apptics.analytics.AppticsEvents

object AppticsTracker {

    // Apptics event wrapper
    fun trackEvent(
        eventName: String,
        eventGroup: String,
        properties: HashMap<String, Any> = hashMapOf(),
    ) {

        AppticsEvents.addEvent(
            eventName,
            eventGroup,
            properties
        )
    }


}
