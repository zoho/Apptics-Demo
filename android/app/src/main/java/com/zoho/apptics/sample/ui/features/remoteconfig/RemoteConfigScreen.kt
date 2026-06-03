package com.zoho.apptics.sample.ui.features.remoteconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.remoteconfig.AppticsRemoteConfig

@Composable
fun RemoteConfigScreen(onBack: () -> Unit) {
    var paramKey by remember { mutableStateOf("welcome_message") }
    var fetched by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var inFlight by remember { mutableStateOf(false) }
    var lastError by remember { mutableStateOf<String?>(null) }

    FeatureScaffold(
        title = "Remote Config",
        description = "Read values from the Apptics console at runtime to gate features, change copy, or run experiments without shipping a new build.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Fetch a value",
            subtitle = "Provide the parameter key you configured in the Apptics console. The result lands in the live panel below."
        ) {
            LabeledTextField(value = paramKey, onValueChange = { paramKey = it }, label = "Parameter key")
            RunButton(
                label = if (inFlight) "Fetching…" else "Fetch value",
                icon = Icons.Filled.CloudSync,
                enabled = !inFlight && paramKey.isNotBlank()
            ) {
                inFlight = true
                lastError = null
                runCatching {
                    // Asks the Apptics console for the value of a parameter
                    // by key. Uses the offline cached value first (if any)
                    // then refreshes from server; onComplete fires with the
                    // final value on the main thread.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-remote_configuration.html
                    AppticsRemoteConfig.fetchValue(
                        paramName = paramKey,
                        onComplete = { data ->
                            val value = data ?: "(no value)"
                            fetched = (fetched.filterNot { it.first == paramKey } + (paramKey to value))
                                .takeLast(8)
                            inFlight = false
                        }
                    )
                }.onFailure {
                    lastError = it.message ?: it.javaClass.simpleName
                    inFlight = false
                }
            }
        }

        if (fetched.isNotEmpty()) {
            SectionCard(title = "Last fetched values") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    fetched.forEach { (k, v) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                k,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                v,
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        LiveStatePanel(
            rows = listOfNotNull(
                "Fetch in flight" to inFlight.toString(),
                "Last fetched" to (fetched.lastOrNull()?.let { "${it.first} = ${it.second}" } ?: "—"),
                lastError?.let { "Last error" to it }
            )
        )

        CodeBlock(
            code = """
                AppticsRemoteConfig.fetchValue("welcome_message") { value ->
                    val message = value ?: "Welcome!"
                    // use message to drive UI
                }
            """.trimIndent()
        )
    }
}
