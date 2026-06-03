package com.zoho.apptics.sample.ui.features.logging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.logger.AppticsLogger
import kotlinx.coroutines.launch

private enum class LogLevel(val label: String) { V("Verbose"), D("Debug"), I("Info"), W("Warning"), E("Error") }

@Composable
fun LoggingScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(AppticsLogger.isEnabled()) }
    var tag by remember { mutableStateOf("AppticsSample") }
    var message by remember { mutableStateOf("Hello from the logger playground") }
    var level by remember { mutableStateOf(LogLevel.D) }
    var entriesWritten by remember { mutableStateOf(0) }
    var lastEntry by remember { mutableStateOf("—") }
    val scope = rememberCoroutineScope()

    FeatureScaffold(
        title = "Remote Logging",
        description = "Apptics Logger writes structured logs that are uploaded with the next sync. Useful for diagnosing issues in production builds.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Logger state",
            subtitle = "Toggle the logger off in regulated builds; toggle it on to capture diagnostics."
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Logger enabled", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = enabled,
                    onCheckedChange = { wanted ->
                        // Globally turn the remote logger on/off. Calls to
                        // AppticsLogger.{v,d,i,w,e} are no-ops while disabled.
                        // Docs: https://www.zoho.com/apptics/resources/SDK/android-remote_logger.html
                        if (wanted) AppticsLogger.enable() else AppticsLogger.disable()
                        enabled = AppticsLogger.isEnabled()
                    }
                )
            }
        }

        SectionCard(
            title = "Write a log line",
            subtitle = "Pick a level + tag and submit. Apptics buffers the entry until flush."
        ) {
            LabeledTextField(value = tag, onValueChange = { tag = it }, label = "Tag")
            LabeledTextField(
                value = message,
                onValueChange = { message = it },
                label = "Message",
                singleLine = false
            )
            LevelDropdown(level = level, onSelect = { level = it })
            RunButton(label = "Write log", icon = Icons.AutoMirrored.Filled.Send, enabled = enabled) {
                // Writes a single log line at the chosen level. Lines are
                // buffered locally and uploaded with the next sync (or via
                // flushLogs() — see below).
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-remote_logger.html
                when (level) {
                    LogLevel.V -> AppticsLogger.v(tag, message)
                    LogLevel.D -> AppticsLogger.d(tag, message)
                    LogLevel.I -> AppticsLogger.i(tag, message)
                    LogLevel.W -> AppticsLogger.w(tag, message)
                    LogLevel.E -> AppticsLogger.e(tag, message)
                }
                entriesWritten++
                lastEntry = "[${level.name}] $tag: $message"
            }
        }

        SectionCard(
            title = "Sync logs to Apptics",
            subtitle = "Run a manual flush — otherwise logs upload with the next scheduled sync."
        ) {
            SecondaryButton(label = "Flush logs now", icon = Icons.Filled.CloudUpload) {
                scope.launch {
                    // Pushes any buffered log entries to the Apptics backend.
                    // This is a suspend function — call from a coroutine.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-remote_logger.html
                    runCatching { AppticsLogger.flushLogs() }
                }
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Enabled" to enabled.toString(),
                "Entries written this session" to entriesWritten.toString(),
                "Last entry" to lastEntry
            )
        )

        CodeBlock(
            code = """
                AppticsLogger.enable()
                AppticsLogger.d("AppticsSample", "Hello from the playground")
                AppticsLogger.e("AppticsSample", "Something went wrong", throwable)

                lifecycleScope.launch { AppticsLogger.flushLogs() }
            """.trimIndent()
        )
    }
}

@Composable
private fun LevelDropdown(level: LogLevel, onSelect: (LogLevel) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Level: ${level.label}", style = MaterialTheme.typography.labelLarge)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LogLevel.values().forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.label) },
                    onClick = {
                        onSelect(entry)
                        expanded = false
                    }
                )
            }
        }
    }
}
