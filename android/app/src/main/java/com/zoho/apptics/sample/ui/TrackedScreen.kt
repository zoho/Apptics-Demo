package com.zoho.apptics.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.zoho.apptics.analytics.AppticsScreenTracker
import com.zoho.apptics.sample.analytics.AppticsTracker

/**
 * Wraps a composable so the associated screen is tracked in Apptics.
 */
@Composable
fun TrackedScreen(
    screenName: String,
    content: @Composable () -> Unit,
) {
    DisposableEffect(screenName) {

        val screenId = AppticsScreenTracker.inScreen(screenName)

        onDispose {
            AppticsScreenTracker.outScreen(screenId)
        }
    }
    content()
}
