package com.zoho.apptics.sample.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zoho.apptics.common.AppticsSettings
import com.zoho.apptics.common.AppticsTrackingState

@Composable
fun SettingsRoute() {
    SettingsScreen(
        onEnableFullTracking = {
            AppticsSettings.setTrackingStatus(AppticsTrackingState.USAGE_AND_CRASH_TRACKING_WITH_PII)
        },
        onEnableAnonymousTracking = {
            AppticsSettings.setTrackingStatus(AppticsTrackingState.USAGE_AND_CRASH_TRACKING_WITHOUT_PII)
        },
        onDisableTracking = {
            AppticsSettings.setTrackingStatus(AppticsTrackingState.NO_TRACKING)
        }
    )
}

@Composable
fun SettingsScreen(
    onEnableFullTracking: () -> Unit,
    onEnableAnonymousTracking: () -> Unit,
    onDisableTracking: () -> Unit
) {
    var usageToggle by remember { mutableStateOf(true) }
    var crashToggle by remember { mutableStateOf(true) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Privacy controls",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )

        ToggleRow(
            title = "Usage analytics tracking",
            subtitle = "Simulate enabling usage + crash tracking with PII.",
            checked = usageToggle,
            onCheckedChange = { isChecked ->
                usageToggle = isChecked
                if (isChecked) {
                    onEnableFullTracking()
                } else {
                    onDisableTracking()
                }
            },
        )

        ToggleRow(
            title = "Crash tracking",
            subtitle = "Flip to anonymous mode or disable everything.",
            checked = crashToggle,
            onCheckedChange = { isChecked ->
                crashToggle = isChecked
                if (isChecked) {
                    onEnableAnonymousTracking()
                } else {
                    onDisableTracking()
                }
            },
        )
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
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
