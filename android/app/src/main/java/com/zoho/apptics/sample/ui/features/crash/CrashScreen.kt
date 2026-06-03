package com.zoho.apptics.sample.ui.features.crash

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.crash.AppticsNonFatals

@Composable
fun CrashScreen(onBack: () -> Unit) {
    var nonFatalMsg by remember { mutableStateOf("Sample non-fatal — operation timed out") }
    var lastAction by remember { mutableStateOf("—") }
    var nonFatalsRecorded by remember { mutableStateOf(0) }
    var showAnrConfirm by remember { mutableStateOf(false) }
    var showCrashConfirm by remember { mutableStateOf(false) }

    FeatureScaffold(
        title = "Crash Tracking",
        description = "Exercise the crash + non-fatal reporting pipeline. Apptics will surface stack traces in your dashboard after the next session.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Force a fatal crash",
            subtitle = "Throws an uncaught exception. The app will close — reopen it to confirm Apptics uploads the crash."
        ) {
            RunButton(label = "Force NPE crash", icon = Icons.Filled.Bolt) {
                showCrashConfirm = true
            }
        }

        SectionCard(
            title = "Trigger an ANR",
            subtitle = "Blocks the main thread for 6 seconds. Android will surface an Application Not Responding dialog and Apptics will record it."
        ) {
            RunButton(label = "Trigger ANR (blocks UI)", icon = Icons.Filled.HourglassEmpty) {
                showAnrConfirm = true
            }
        }

        SectionCard(
            title = "Record a non-fatal exception",
            subtitle = "Reports a caught throwable. Use this for handled errors you still want to track."
        ) {
            LabeledTextField(
                value = nonFatalMsg,
                onValueChange = { nonFatalMsg = it },
                label = "Exception message"
            )
            RunButton(label = "Record non-fatal", icon = Icons.Filled.ErrorOutline) {
                try {
                    throw RuntimeException(nonFatalMsg)
                } catch (t: Throwable) {
                    // Records a caught throwable as a non-fatal in the Apptics
                    // dashboard. Fatal/uncaught crashes are picked up automatically
                    // — only call this for errors you handled but still want to
                    // surface for diagnostics.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-crashreporting.html
                    AppticsNonFatals.recordException(t)
                }
                nonFatalsRecorded++
                lastAction = "recordException(\"$nonFatalMsg\")"
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Non-fatals recorded" to nonFatalsRecorded.toString(),
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                // Non-fatal
                try { riskyCall() }
                catch (t: Throwable) { AppticsNonFatals.recordException(t) }

                // Fatal — let the exception propagate
                throw NullPointerException("forced crash")
            """.trimIndent()
        )
    }

    if (showAnrConfirm) {
        AlertDialog(
            onDismissRequest = { showAnrConfirm = false },
            title = { Text("Trigger ANR?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    "This blocks the main thread for 6 seconds. Android will show an ANR dialog and the app may be killed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showAnrConfirm = false
                    val deadline = System.currentTimeMillis() + 6_000
                    while (System.currentTimeMillis() < deadline) {
                        // intentional main-thread block
                    }
                }) { Text("Trigger") }
            },
            dismissButton = {
                TextButton(onClick = { showAnrConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showCrashConfirm) {
        AlertDialog(
            onDismissRequest = { showCrashConfirm = false },
            title = { Text("Crash the app?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    "The process will terminate. Reopen the app afterwards — Apptics will upload the crash on the next session.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showCrashConfirm = false
                    val nothing: String? = null
                    nothing!!.length // forces NPE
                }) { Text("Crash") }
            },
            dismissButton = {
                TextButton(onClick = { showCrashConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
