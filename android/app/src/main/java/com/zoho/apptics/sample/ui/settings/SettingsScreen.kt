package com.zoho.apptics.sample.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoho.apptics.common.AppticsSettings
import com.zoho.apptics.common.AppticsTrackingState
import com.zoho.apptics.feedback.AppticsFeedback
import com.zoho.apptics.sample.ui.findActivity

@Composable
fun SettingsRoute() {
    SettingsScreen()
}

@Composable
fun SettingsScreen() {

    val context = LocalContext.current

    var shakeForFeedbackStatus by remember {
        mutableStateOf(AppticsFeedback.isShakeForFeedbackEnabled())
    }

    LaunchedEffect(shakeForFeedbackStatus) {
        AppticsFeedback.shakeDialogDontShowAgainCallback = {
            shakeForFeedbackStatus = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        PrivacyControls(
            onSettingsChanged = { trackingState ->
                AppticsSettings.setTrackingStatus(trackingState)
            }
        )

        HorizontalDivider(thickness = 1.dp)

        FeedbackControls(
            shakeForFeedbackStatus = shakeForFeedbackStatus,
            openFeedback = {
                val activity = context.findActivity()
                activity?.let {
                    AppticsFeedback.openFeedback(it)
                }
            },
            toggleShakeForFeedback = { status ->
                if (status) {
                    AppticsFeedback.enableShakeForFeedback()
                } else {
                    AppticsFeedback.disableShakeForFeedback()
                }
            }
        )
    }

}

// Determin AppticsTrackingState based on toggle switches status
private fun getTrackingState(
    isUsageTrackingEnabled: Boolean,
    isCrashTrackingEnabled: Boolean,
    isSendAnonToggleEnable: Boolean
): AppticsTrackingState {
    if (isSendAnonToggleEnable) {
        if (isUsageTrackingEnabled && isCrashTrackingEnabled) {
            return AppticsTrackingState.USAGE_AND_CRASH_TRACKING_WITH_PII
        } else if (isUsageTrackingEnabled) {
            return AppticsTrackingState.ONLY_USAGE_TRACKING_WITH_PII
        } else if (isCrashTrackingEnabled) {
            return AppticsTrackingState.ONLY_CRASH_TRACKING_WITH_PII
        }
    } else {
        if (isUsageTrackingEnabled && isCrashTrackingEnabled) {
            return AppticsTrackingState.USAGE_AND_CRASH_TRACKING_WITHOUT_PII
        } else if (isUsageTrackingEnabled) {
            return AppticsTrackingState.ONLY_USAGE_TRACKING_WITHOUT_PII
        } else if (isCrashTrackingEnabled) {
            return AppticsTrackingState.ONLY_CRASH_TRACKING_WITHOUT_PII
        }
    }
    return AppticsTrackingState.NO_TRACKING
}

@Composable
private fun FeedbackControls(
    shakeForFeedbackStatus: Boolean,
    openFeedback: () -> Unit,
    toggleShakeForFeedback: (Boolean) -> Unit
) {

    var shakeToggle by remember { mutableStateOf(shakeForFeedbackStatus) }

    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Feedback",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )

        ToggleRow(
            title = "Shake for feedback",
            subtitle = "Shake the device to open feedback options",
            checked = shakeToggle,
            onCheckedChange = { isChecked ->
                shakeToggle = isChecked
                toggleShakeForFeedback.invoke(shakeToggle)
            },
        )

        ActionRow(
            title = "Write to us",
            subtitle = "Open the feedback form to send us a message",
            actionLabel = "Open",
            onClick = {
                openFeedback.invoke()
            },
        )
    }

}

@Composable
private fun PrivacyControls(
    onSettingsChanged: (AppticsTrackingState) -> Unit
) {
    var usageToggle by remember { mutableStateOf(true) }
    var crashToggle by remember { mutableStateOf(true) }
    var sendAnonToggle by remember { mutableStateOf(true) }


    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Privacy controls",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )

        ToggleRow(
            title = "Usage analytics tracking",
            subtitle = "Allow app to send usage details",
            checked = usageToggle,
            onCheckedChange = { isChecked ->
                usageToggle = isChecked
                onSettingsChanged.invoke(getTrackingState(
                    usageToggle,
                    crashToggle,
                    sendAnonToggle
                ))
            },
        )

        ToggleRow(
            title = "Crash tracking",
            subtitle = "Allow app to send crash reports",
            checked = crashToggle,
            onCheckedChange = { isChecked ->
                crashToggle = isChecked
                onSettingsChanged.invoke(getTrackingState(
                    usageToggle,
                    crashToggle,
                    sendAnonToggle
                ))
            },
        )

        ToggleRow(
            title = "Send Anonymously",
            subtitle = "Send data without any user identifiable information",
            checked = sendAnonToggle,
            onCheckedChange = { isChecked ->
                sendAnonToggle = isChecked
                onSettingsChanged.invoke(getTrackingState(
                    usageToggle,
                    crashToggle,
                    sendAnonToggle
                ))
            },
        )

        Spacer(modifier = Modifier.height(24.dp))


    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    subtitle: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onClick) {
            Text(actionLabel)
        }
    }
}
