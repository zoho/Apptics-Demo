package com.zoho.apptics.sample.ui.features.feedback

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.feedback.AppticsFeedback

@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var shakeEnabled by remember {
        mutableStateOf(runCatching { AppticsFeedback.isShakeForFeedbackEnabled() }.getOrDefault(false))
    }
    var lastAction by remember { mutableStateOf("—") }

    FeatureScaffold(
        title = "In-App Feedback",
        description = "Open Apptics' built-in feedback UI, fire a bug-report flow, or let users shake the device to send feedback.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Launch a feedback flow",
            subtitle = "Both screens are owned by Apptics — they include attachments, screenshots and annotation tools."
        ) {
            RunButton(label = "Open feedback UI", icon = Icons.AutoMirrored.Filled.Chat) {
                (context as? Activity)?.let {
                    // Launches the Apptics-owned feedback screen (composer +
                    // attachments + auto-collected diagnostics).
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_feedback.html
                    AppticsFeedback.openFeedback(it)
                    lastAction = "openFeedback(activity)"
                }
            }
            SecondaryButton(label = "Open bug report UI", icon = Icons.Filled.BugReport) {
                (context as? Activity)?.let { activity ->
                    runCatching {
                        // Variant of openFeedback that auto-captures a
                        // screenshot of the current screen and opens it in the
                        // annotation editor. Use for bug-report flows.
                        // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_feedback.html
                        AppticsFeedback.reportBug(activity)
                        lastAction = "reportBug(activity)"
                    }.onFailure {
                        AppticsFeedback.openFeedback(activity)
                        lastAction = "openFeedback (reportBug not available in this SDK version)"
                    }
                }
            }
        }

        SectionCard(
            title = "Shake-to-send",
            subtitle = "Users can shake their phone anywhere in the app to trigger the feedback screen."
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Shake detection", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = shakeEnabled,
                    onCheckedChange = { wanted ->
                        // Toggles shake-to-feedback. When enabled, the SDK
                        // registers a SensorManager listener and opens the
                        // feedback screen automatically on a strong shake.
                        // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_feedback.html
                        if (wanted) AppticsFeedback.enableShakeForFeedback()
                        else AppticsFeedback.disableShakeForFeedback()
                        shakeEnabled = AppticsFeedback.isShakeForFeedbackEnabled()
                        lastAction = if (shakeEnabled) "enableShakeForFeedback()" else "disableShakeForFeedback()"
                    }
                )
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Shake enabled" to shakeEnabled.toString(),
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                AppticsFeedback.openFeedback(activity)

                // Shake-to-feedback
                if (AppticsFeedback.isShakeForFeedbackEnabled()) {
                    AppticsFeedback.disableShakeForFeedback()
                } else {
                    AppticsFeedback.enableShakeForFeedback()
                }
            """.trimIndent()
        )
    }
}
