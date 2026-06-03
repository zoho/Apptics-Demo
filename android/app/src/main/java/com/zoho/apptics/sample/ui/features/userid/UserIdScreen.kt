package com.zoho.apptics.sample.ui.features.userid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.common.AppticsUser
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun UserIdScreen(onBack: () -> Unit) {
    var userId by remember { mutableStateOf("demo@apptics.dev") }
    var orgId by remember { mutableStateOf("") }
    var lastAction by remember { mutableStateOf("—") }
    var refreshTick by remember { mutableStateOf(0) }
    var currentUserId by remember { mutableStateOf("…") }
    var currentOrgId by remember { mutableStateOf("…") }

    LaunchedEffect(refreshTick) {
        val info = withContext(Dispatchers.IO) {
            // Reads the user currently attached to this device. Hits the local DB,
            // so the call is @WorkerThread — keep it off the main dispatcher.
            // Docs: https://www.zoho.com/apptics/resources/SDK/android-users.html
            runCatching { AppticsUser.getCurrentUserInfo() }.getOrNull()
        }
        currentUserId = info?.userId?.takeIf { it.isNotBlank() } ?: "not set"
        currentOrgId = info?.orgId ?: "—"
    }

    FeatureScaffold(
        title = "Identify User",
        description = "Associate the device with a real user so events, crashes and feedback are attributed correctly in the Apptics dashboard.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Set the current user",
            subtitle = "User ID is opaque to Apptics — use whatever identifier your backend already uses."
        ) {
            LabeledTextField(value = userId, onValueChange = { userId = it }, label = "User ID")
            LabeledTextField(
                value = orgId,
                onValueChange = { orgId = it },
                label = "Org ID (optional)",
                supportingText = "Leave blank to use setUser; otherwise setUserWithOrgId is called."
            )
            RunButton(label = "Set user", icon = Icons.Filled.PersonAdd) {
                // Associates the device with a user. Use setUserWithOrgId if your
                // backend models users under an organisation; otherwise setUser.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-users.html
                if (orgId.isBlank()) {
                    AppticsUser.setUser(userId = userId)
                } else {
                    AppticsUser.setUserWithOrgId(userId = userId, orgId = orgId)
                }
                lastAction = if (orgId.isBlank()) "setUser($userId)" else "setUserWithOrgId($userId, $orgId)"
                refreshTick++
            }
        }

        SectionCard(title = "Clear the current user") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    label = "Remove current",
                    icon = Icons.Filled.PersonRemove,
                    modifier = Modifier.weight(1f)
                ) {
                    // Dissociates the active user from this device. Subsequent
                    // events become anonymous until setUser is called again.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-users.html
                    AppticsUser.removeCurrentUser()
                    lastAction = "removeCurrentUser()"
                    refreshTick++
                }
                SecondaryButton(
                    label = "Refresh",
                    icon = Icons.Filled.Refresh,
                    modifier = Modifier.weight(1f)
                ) {
                    refreshTick++
                }
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Current user" to currentUserId,
                "Org" to currentOrgId,
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                // Set the user
                AppticsUser.setUser(userId = "demo@apptics.dev")

                // Set with org
                AppticsUser.setUserWithOrgId(
                    userId = "demo@apptics.dev",
                    orgId = "ACME-123"
                )

                // Clear
                AppticsUser.removeCurrentUser()

                // Read (worker thread — runs blocking DB read)
                val info = AppticsUser.getCurrentUserInfo()
            """.trimIndent()
        )
    }
}
