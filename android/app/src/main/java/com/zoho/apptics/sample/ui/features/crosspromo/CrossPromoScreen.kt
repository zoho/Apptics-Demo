package com.zoho.apptics.sample.ui.features.crosspromo

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
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
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.crosspromotion.AppticsCrossPromotion

@Composable
fun CrossPromoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var lastAction by remember { mutableStateOf("—") }

    FeatureScaffold(
        title = "Cross-Promotion",
        description = "Show a curated gallery of other apps from your organisation. The contents are configured in the Apptics console.",
        onBack = onBack
    ) {
        SectionCard(
            title = "Open cross-promotion",
            subtitle = "Launches the Apptics-owned gallery screen. Cards & badges are pulled from the console."
        ) {
            RunButton(label = "Open cross-promotion", icon = Icons.Filled.Campaign) {
                (context as? Activity)?.let {
                    runCatching {
                        // Launches the Apptics-owned cross-promotion gallery
                        // Activity. Cards / badges / images shown inside are
                        // configured per-app on the Apptics web console.
                        // Docs: https://www.zoho.com/apptics/resources/SDK/android-cross_promotion.html
                        AppticsCrossPromotion.startActivity(it)
                        lastAction = "AppticsCrossPromotion.startActivity()"
                    }.onFailure { e ->
                        lastAction = "Failed: ${e.message}"
                    }
                }
            }
        }

        LiveStatePanel(
            rows = listOf("Last action" to lastAction)
        )

        CodeBlock(
            code = """
                AppticsCrossPromotion.startActivity(activity)
            """.trimIndent()
        )
    }
}
