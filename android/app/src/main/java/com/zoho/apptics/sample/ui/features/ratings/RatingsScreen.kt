package com.zoho.apptics.sample.ui.features.ratings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.input.KeyboardType
import com.zoho.apptics.analytics.AppticsEvents
import com.zoho.apptics.rateus.AppticsInAppRatings
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LabeledTextField
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard

@Composable
fun RatingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var lastAction by remember { mutableStateOf("—") }
    var daysBefore by remember { mutableStateOf(AppticsInAppRatings.daysBeforeShowingPopupAgain.toString()) }
    var maxTimes by remember { mutableStateOf(AppticsInAppRatings.maxTimesToShowPopup.toString()) }
    var autoDisabled by remember { mutableStateOf(AppticsInAppRatings.disableAutoPromptOnFulFillingCriteria) }
    var playCoreOnFulfil by remember { mutableStateOf(AppticsInAppRatings.showAndroidPlayCoreAlertOnFulFillingCriteria) }
    AppticsEvents.addEvent("IN_APP_RATING_ADD_CRITERIA","IN_APP_RATING")
    FeatureScaffold(
        title = "In-App Ratings",
        description = "Criteria are configured in the Apptics console. The SDK fires the popup automatically when conditions are met; here you can tune the timing knobs and trigger it manually.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Cooldown & limits",
            subtitle = "Apply locally before the next session — these defaults are reasonable for most apps."
        ) {
            LabeledTextField(
                value = daysBefore,
                onValueChange = { daysBefore = it.filter(Char::isDigit) },
                label = "Days before showing again",
                keyboardType = KeyboardType.Number
            )
            LabeledTextField(
                value = maxTimes,
                onValueChange = { maxTimes = it.filter(Char::isDigit) },
                label = "Max times to show",
                keyboardType = KeyboardType.Number
            )
            SecondaryButton(label = "Apply") {
                // Cooldown knobs: how long to wait before re-prompting after a
                // dismissal, and the lifetime cap on how many times the popup
                // can appear for one user. Tune to your app's session cadence.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html#defaultratingpop-up
                AppticsInAppRatings.daysBeforeShowingPopupAgain = daysBefore.toIntOrNull() ?: 10
                AppticsInAppRatings.maxTimesToShowPopup = maxTimes.toIntOrNull() ?: 3
                lastAction = "daysBefore=${AppticsInAppRatings.daysBeforeShowingPopupAgain}, max=${AppticsInAppRatings.maxTimesToShowPopup}"
            }
        }

        SectionCard(
            title = "Auto-prompt behavior",
            subtitle = "Switch off to suppress the popup even when criteria are met; switch on Play Core to use Google's in-app review UI."
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Disable auto-prompt", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = autoDisabled,
                    onCheckedChange = {
                        // When true, the SDK won't auto-show the rate-us
                        // popup even after criteria are met — you must call
                        // showPopupIfCriteriaFulfilled() yourself.
                        autoDisabled = it
                        AppticsInAppRatings.disableAutoPromptOnFulFillingCriteria = it
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Use Play Core alert on criteria", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = playCoreOnFulfil,
                    onCheckedChange = {
                        // When true, the SDK delegates the rate-us dialog to
                        // Google Play Core's in-app review UI instead of
                        // showing Apptics' own popup.
                        // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html#googleplayin-appreviewapi
                        playCoreOnFulfil = it
                        AppticsInAppRatings.showAndroidPlayCoreAlertOnFulFillingCriteria = it
                    }
                )
            }
        }

        SectionCard(
            title = "Trigger manually",
            subtitle = "Either probe the SDK to see if criteria are met now, or open the Play Store entry directly."
        ) {
            RunButton(label = "Show popup if criteria fulfilled", icon = Icons.Filled.Star) {
                // Asks the SDK whether the console-configured criteria (event
                // count, screen visits, session count, etc.) are satisfied
                // for this user; if so, displays the rate-us popup.
                // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html
                AppticsEvents.addEvent("IN_APP_RATING_ADD_TRIGGER","IN_APP_RATING")
                lastAction = "showPopupIfCriteriaFulfilled()"
            }
            SecondaryButton(label = "Open Play Store entry", icon = Icons.AutoMirrored.Filled.OpenInNew) {
                (context as? Activity)?.let {
                    // Sends the user straight to your app's Play Store page,
                    // bypassing the criteria-based popup flow entirely.
                    // Docs: https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html
                    AppticsInAppRatings.openStore(it)
                    lastAction = "openStore(activity)"
                }
            }
        }

        LiveStatePanel(
            rows = listOf(
                "Days before next prompt" to AppticsInAppRatings.daysBeforeShowingPopupAgain.toString(),
                "Max shows" to AppticsInAppRatings.maxTimesToShowPopup.toString(),
                "Auto-prompt disabled" to autoDisabled.toString(),
                "Use Play Core" to playCoreOnFulfil.toString(),
                "Last action" to lastAction
            )
        )

        CodeBlock(
            code = """
                AppticsInAppRatings.daysBeforeShowingPopupAgain = 10
                AppticsInAppRatings.maxTimesToShowPopup = 3
                AppticsInAppRatings.disableAutoPromptOnFulFillingCriteria = false

                AppticsInAppRatings.showPopupIfCriteriaFulfilled()
                AppticsInAppRatings.openStore(activity)
            """.trimIndent()
        )
    }
}
