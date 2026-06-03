package com.zoho.apptics.sample.ui.features.push

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zoho.apptics.sample.push.LastPushState
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard

@Composable
fun PushScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var rawToken by remember { mutableStateOf(LastPushState.token ?: "fetching…") }
    var permissionStatus by remember { mutableStateOf(notificationPermissionState(context)) }
    var lastAction by remember { mutableStateOf("—") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionStatus = if (granted) "granted" else "denied"
    }

    // Reflection-based call to FirebaseMessaging.getInstance().token so the
    // sample compiles even when the firebase-messaging dependency is commented
    // out. In a real integration with Firebase configured, this whole block
    // simplifies to:
    //
    //     FirebaseMessaging.getInstance().token
    //         .addOnSuccessListener { token ->
    //             AppticsPushNotification.onNewToken(token)  // forward to Apptics
    //         }
    //
    // Docs: https://www.zoho.com/apptics/resources/SDK/android-push-notifications.html
    LaunchedEffect(Unit) {
        runCatching {
            val firebase = Class.forName("com.google.firebase.messaging.FirebaseMessaging")
            val instance = firebase.getMethod("getInstance").invoke(null)
            val taskMethod = instance.javaClass.getMethod("getToken")
            val task = taskMethod.invoke(instance)
            val addListenerMethod = task.javaClass.methods.first {
                it.name == "addOnSuccessListener" && it.parameterTypes.size == 1
            }
            val listener = java.lang.reflect.Proxy.newProxyInstance(
                addListenerMethod.parameterTypes[0].classLoader,
                arrayOf(addListenerMethod.parameterTypes[0])
            ) { _, _, args ->
                val newToken = args?.firstOrNull() as? String
                if (!newToken.isNullOrBlank()) {
                    LastPushState.token = newToken
                    rawToken = newToken
                }
                null
            }
            addListenerMethod.invoke(task, listener)
        }.onFailure {
            rawToken = "Firebase unavailable: ${it.message}"
        }
    }

    val firebaseMissing = rawToken.startsWith("Firebase unavailable")

    FeatureScaffold(
        title = "Push Notifications",
        description = "Apptics piggy-backs on Firebase Cloud Messaging. Once the SDK has your token, you can target this device from the Apptics console.",
        onBack = onBack
    ) {
        if (firebaseMissing) {
            FirebaseNotConfiguredCard()
        }

        SectionCard(
            title = "Notification permission",
            subtitle = "Android 13+ requires runtime permission to display notifications."
        ) {
            RunButton(label = "Request POST_NOTIFICATIONS", icon = Icons.Filled.NotificationsActive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    lastAction = "Asked for POST_NOTIFICATIONS"
                } else {
                    lastAction = "Not required pre-Android 13"
                    permissionStatus = "granted (legacy)"
                }
            }
        }

        SectionCard(
            title = "FCM token",
            subtitle = "Copy this token into the Apptics console (or your test sender) to deliver a push to this device."
        ) {
            SecondaryButton(label = "Copy token", icon = Icons.Filled.ContentCopy) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("FCM token", rawToken))
                lastAction = "Copied token"
            }
            SecondaryButton(label = "Refresh", icon = Icons.Filled.Refresh) {
                rawToken = LastPushState.token ?: "fetching…"
                lastAction = "Refreshed token"
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Permission" to permissionStatus,
                "FCM token" to displayToken(rawToken),
                "Last message" to (LastPushState.lastMessage ?: "—"),
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                // In your FirebaseMessagingService
                override fun onNewToken(token: String) {
                    AppticsPushNotification.onNewToken(token)
                }
                override fun onMessageReceived(message: RemoteMessage) {
                    AppticsPushNotification.handleMessage(applicationContext, message)
                }

                // AndroidManifest
                <service android:name=".push.SampleFcmService" android:exported="false">
                    <intent-filter>
                        <action android:name="com.google.firebase.MESSAGING_EVENT" />
                    </intent-filter>
                </service>
            """.trimIndent()
        )
    }
}

@Composable
private fun FirebaseNotConfiguredCard() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "Firebase not configured",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                "Push notifications are disabled in this sample. `google-services.json` is missing, the `apptics-pns` + `firebase-messaging` dependencies are commented out in `app/build.gradle.kts`, and the FCM service is commented out in `AndroidManifest.xml`. Token retrieval will fail until you wire up a real Firebase project.",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (expanded) "Hide setup steps" else "How to enable",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StepRow(
                        "1.",
                        "Create a Firebase project, add an Android app with applicationId \"com.zoho.apptics.sample\", and download google-services.json into the app/ directory."
                    )
                    StepRow(
                        "2.",
                        "Add com.google.gms:google-services to the project classpath and apply id(\"com.google.gms.google-services\") in app/build.gradle.kts."
                    )
                    StepRow(
                        "3.",
                        "Uncomment implementation(libs.apptics.pns) and implementation(libs.firebase.messaging) in app/build.gradle.kts."
                    )
                    StepRow(
                        "4.",
                        "Uncomment the <service> block for SampleFcmService in AndroidManifest.xml and restore the SampleFcmService source (recover it from git history if it was removed)."
                    )
                    StepRow(
                        "5.",
                        "Rebuild and reinstall — the live-state panel will then show a real FCM token."
                    )
                }
            }
        }
    }
}

@Composable
private fun StepRow(number: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            number,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun displayToken(token: String): String = when {
    token.startsWith("Firebase unavailable") -> "not available"
    token == "fetching…" -> token
    token.length > 24 -> "${token.take(8)}…${token.takeLast(8)}"
    else -> token
}

private fun notificationPermissionState(context: Context): String {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        "granted (legacy)"
    } else {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) "granted" else "not granted"
    }
}
