package com.zoho.apptics.sample.ui.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object UserId : Route("user")
    data object Analytics : Route("analytics")
    data object Crash : Route("crash")
    data object Logging : Route("logging")
    data object Feedback : Route("feedback")
    data object RemoteConfig : Route("remote-config")
    data object Ratings : Route("ratings")
    data object Updates : Route("updates")
    data object CrossPromo : Route("cross-promo")
    data object Push : Route("push")
}
