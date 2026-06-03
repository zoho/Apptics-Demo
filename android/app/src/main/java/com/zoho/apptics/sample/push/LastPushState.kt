package com.zoho.apptics.sample.push

/**
 * In-memory store for the most recently observed FCM token / push payload.
 *
 * The bundled sample doesn't ship a real FirebaseMessagingService — wiring that up
 * requires google-services.json + the com.google.gms.google-services plugin, which
 * varies per integrator. Once you add a service, write to these fields and the
 * Push screen will reflect the state.
 */
object LastPushState {
    @Volatile var token: String? = null
    @Volatile var lastMessage: String? = null
}
