package com.zoho.apptics.sample.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zoho.apptics.sample.ui.navigation.Route

private data class FeatureEntry(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: Route
)

private data class FeatureGroup(
    val name: String,
    val tagline: String,
    val features: List<FeatureEntry>
)

private val groups = listOf(
    FeatureGroup(
        name = "Identity",
        tagline = "Who's using the app",
        features = listOf(
            FeatureEntry(
                "Identify user",
                "setUser, setUserWithOrgId, removeCurrentUser",
                Icons.Filled.Person,
                Route.UserId
            )
        )
    ),
    FeatureGroup(
        name = "Analytics",
        tagline = "Events, screens, properties",
        features = listOf(
            FeatureEntry(
                "Events & screens",
                "addEvent, flush, consent + tracking popups",
                Icons.Filled.Analytics,
                Route.Analytics
            )
        )
    ),
    FeatureGroup(
        name = "Diagnostics",
        tagline = "Crashes & remote logs",
        features = listOf(
            FeatureEntry(
                "Crash tracking",
                "Force crash, ANR, non-fatals, toggles",
                Icons.Filled.BugReport,
                Route.Crash
            ),
            FeatureEntry(
                "Remote logging",
                "v/d/i/w/e levels, flush, enable toggle",
                Icons.Filled.Terminal,
                Route.Logging
            )
        )
    ),
    FeatureGroup(
        name = "Monitoring",
        tagline = "API success rate & latency",
        features = listOf(
            FeatureEntry(
                "API tracking",
                "Auto interceptor, normalization, filtering, manual",
                Icons.Filled.Api,
                Route.ApiTracking
            )
        )
    ),
    FeatureGroup(
        name = "Engagement",
        tagline = "Ratings, updates, cross-promo",
        features = listOf(
            FeatureEntry(
                "In-app ratings",
                "Criteria, show dialog, rate-us config",
                Icons.Filled.Star,
                Route.Ratings
            ),
            FeatureEntry(
                "In-app updates",
                "Version alert, flexible & immediate",
                Icons.Filled.SystemUpdate,
                Route.Updates
            ),
            FeatureEntry(
                "Cross-promotion",
                "Launch the cross-promo gallery",
                Icons.Filled.Campaign,
                Route.CrossPromo
            )
        )
    ),
    FeatureGroup(
        name = "Feedback",
        tagline = "Hear from your users",
        features = listOf(
            FeatureEntry(
                "In-app feedback",
                "Feedback UI, bug report, shake-to-send",
                Icons.AutoMirrored.Filled.Chat,
                Route.Feedback
            )
        )
    ),
    FeatureGroup(
        name = "Configuration",
        tagline = "Toggle features remotely",
        features = listOf(
            FeatureEntry(
                "Remote config",
                "Fetch values, conditions, defaults",
                Icons.Filled.Tune,
                Route.RemoteConfig
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Route) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Apptics Sample",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Every Apptics feature in one place",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(groups) { group ->
                FeatureGroupSection(group = group, onNavigate = onNavigate)
            }
            item {
                FooterCard()
            }
        }
    }
}

@Composable
private fun FeatureGroupSection(group: FeatureGroup, onNavigate: (Route) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                group.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                group.tagline,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        group.features.forEach { feature ->
            FeatureRow(feature = feature, onClick = { onNavigate(feature.route) })
        }
    }
}

@Composable
private fun FeatureRow(feature: FeatureEntry, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    feature.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    feature.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FooterCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "About this sample",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Each screen is a live playground for one Apptics capability. Open a screen, tweak inputs, hit Run, and watch the live state panel + console reflect what the SDK is doing.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
