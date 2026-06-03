package com.zoho.apptics.sample.ui.features.updates

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.appupdates.AppticsInAppUpdates

@Composable
fun UpdatesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var lastAction by remember { mutableStateOf("—") }

    FeatureScaffold(
        title = "In-App Updates",
        description = "Prompt users to install newer builds. Apptics first checks the console-configured version, then surfaces an upgrade dialog when needed.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Check for an update",
            subtitle = "If the configured version is newer than the installed app, Apptics shows the upgrade prompt."
        ) {
            RunButton(label = "Check & show version alert", icon = Icons.Filled.SystemUpdate) {
                (context as? AppCompatActivity)?.let {
                    // Standard entry point — checks the console-configured
                    // target version against this build and shows the Apptics
                    // upgrade dialog if an update is available.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_update.html
                    AppticsInAppUpdates.checkAndShowVersionAlert(it)
                    lastAction = "checkAndShowVersionAlert()"
                }
            }
            SecondaryButton(label = "Cold-check for update (worker thread)") {
                Thread {
                    // Bypasses the SDK's cached update info and hits the
                    // network directly. @WorkerThread — keep off the main thread.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_update.html
                    val data = runCatching { AppticsInAppUpdates.coldCheckForUpdate() }.getOrNull()
                    lastAction = if (data != null) "Update available: $data" else "No update available"
                }.start()
            }
            SecondaryButton(label = "Install pending flexible update") {
                // Triggers the install step after a Play Core flexible update
                // has finished downloading. No-op if nothing is queued.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_update.html
                runCatching { AppticsInAppUpdates.installFlexibleUpdate() }
                    .onFailure { lastAction = "installFlexibleUpdate failed: ${it.message}" }
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Disable when not from Play Store" to AppticsInAppUpdates.disableIfNotInstalledFromPlayStore.toString(),
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                AppticsInAppUpdates.checkAndShowVersionAlert(activity)

                // Force a fresh check (worker thread)
                val data = AppticsInAppUpdates.coldCheckForUpdate()

                // After a flexible update downloads
                AppticsInAppUpdates.installFlexibleUpdate()
            """.trimIndent()
        )
    }
}
