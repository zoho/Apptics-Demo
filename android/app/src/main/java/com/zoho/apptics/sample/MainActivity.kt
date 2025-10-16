package com.zoho.apptics.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zoho.apptics.sample.ui.theme.AppticsAndroidTheme
import com.zoho.apptics.sample.ui.TrackedScreen
import com.zoho.apptics.sample.ui.home.TodoHomeRoute
import com.zoho.apptics.sample.ui.settings.SettingsRoute
import androidx.compose.runtime.remember
import com.zoho.apptics.sample.analytics.AppticsTracker
import com.zoho.apptics.sample.analytics.TodoEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppticsAndroidTheme {
                val onTodoEvent = remember {
                    { event: TodoEvent ->
                        AppticsTracker.trackEvent(
                            eventName = event.eventName,
                            eventGroup = event.groupName
                        )
                    }
                }

                var selectedDestination by rememberSaveable { mutableStateOf(MainDestination.HOME) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            MainDestination.values().forEach { destination ->
                                val selected = destination == selectedDestination
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { selectedDestination = destination },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.label,
                                        )
                                    },
                                    label = { Text(destination.label) },
                                )
                            }
                        }
                    },
                ) { padding ->
                    when (selectedDestination) {
                        MainDestination.HOME ->
                            TrackedScreen(selectedDestination.screenName) {
                                Box(modifier = Modifier.padding(padding)) {
                                    TodoHomeRoute(onEvent = onTodoEvent)
                                }
                            }
                        MainDestination.SETTINGS ->
                            TrackedScreen(selectedDestination.screenName) {
                                Box(modifier = Modifier.padding(padding)) {
                                    SettingsRoute()
                                }
                            }
                    }
                }
            }
        }
    }
}

private enum class MainDestination(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screenName: String,
) {
    HOME("Home", Icons.Filled.List, "todo_home"),
    SETTINGS("Settings", Icons.Filled.Settings, "privacy_settings"),
}
