package com.zoho.apptics.sample.ui.features.analytics

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.KeyValueEditor
import com.zoho.apptics.sample.ui.components.KvEntry
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.analytics.AppticsAnalytics
import com.zoho.apptics.analytics.AppticsEvents
import org.json.JSONObject

@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var eventName by remember { mutableStateOf("button_tapped") }
    var eventGroup by remember { mutableStateOf("home") }
    var properties by remember { mutableStateOf(listOf(KvEntry("source", "sample-app"))) }
    var sentCount by remember { mutableStateOf(0) }
    var lastPayload by remember { mutableStateOf("—") }

    FeatureScaffold(
        title = "Analytics",
        description = "Fire custom events with optional properties, flush the queue on demand, and surface privacy / consent screens.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Send a custom event",
            subtitle = "Each event has a name + group. Properties are sent as a JSON object."
        ) {
            LabeledTextField(value = eventName, onValueChange = { eventName = it }, label = "Event name")
            LabeledTextField(value = eventGroup, onValueChange = { eventGroup = it }, label = "Event group")
            KeyValueEditor(entries = properties, onChange = { properties = it })
            RunButton(label = "Send event", icon = Icons.AutoMirrored.Filled.Send) {
                val props = properties.filter { it.key.isNotBlank() }
                // Records a custom event. The two-arg overload sends just the
                // name + group; the three-arg overload attaches a JSON payload
                // of custom properties for richer reporting.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_event.html
                if (props.isEmpty()) {
                    AppticsEvents.addEvent(eventName = eventName, eventGroup = eventGroup)
                    lastPayload = "$eventName / $eventGroup (no properties)"
                } else {
                    val json = JSONObject().apply {
                        props.forEach { put(it.key, it.value) }
                    }
                    AppticsEvents.addEvent(
                        eventName = eventName,
                        eventGroup = eventGroup,
                        customProperties = json
                    )
                    lastPayload = "$eventName / $eventGroup → $json"
                }
                sentCount++
            }
        }

        SectionCard(
            title = "Sync & privacy controls",
            subtitle = "Flush queued events immediately, or open Apptics-owned settings & consent UIs."
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    label = "Flush now",
                    icon = Icons.Filled.Sync,
                    modifier = Modifier.weight(1f)
                ) {
                    // Forces queued events / engagement data to upload immediately
                    // instead of waiting for the next sync window.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_event.html
                    AppticsAnalytics.flush()
                }
                SecondaryButton(
                    label = "Tracking settings",
                    icon = Icons.Filled.Settings,
                    modifier = Modifier.weight(1f)
                ) {
                    // Opens the Apptics-owned screen that lets the user toggle
                    // analytics / crash / personal-data tracking categories.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-consent.html
                    (context as? Activity)?.let { AppticsAnalytics.openSettings(it) }
                }
            }
            SecondaryButton(label = "Show consent popup", icon = Icons.Filled.PrivacyTip) {
                // Shows a one-time consent dialog asking the user to review
                // their current tracking preferences. Pass showOnlyOnce = true
                // so the popup never reappears once acknowledged.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-consent.html
                (context as? Activity)?.let {
                    AppticsAnalytics.showReviewTrackingSettingsPopup(it, showOnlyOnce = true)
                }
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Events sent this session" to sentCount.toString(),
                "Last payload" to lastPayload
            )
        )

        CodeBlock(
            code = """
                AppticsEvents.addEvent(
                    eventName = "button_tapped",
                    eventGroup = "home",
                    customProperties = JSONObject().put("source", "sample-app")
                )

                AppticsAnalytics.flush()
                AppticsAnalytics.openSettings(activity)
                AppticsAnalytics.showReviewTrackingSettingsPopup(activity, showOnlyOnce = true)
            """.trimIndent()
        )
    }
}
