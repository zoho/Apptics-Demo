package com.zoho.apptics.sample.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zoho.apptics.sample.ui.features.analytics.AnalyticsScreen
import com.zoho.apptics.sample.ui.features.apitracking.ApiTrackingScreen
import com.zoho.apptics.sample.ui.features.crash.CrashScreen
import com.zoho.apptics.sample.ui.features.crosspromo.CrossPromoScreen
import com.zoho.apptics.sample.ui.features.feedback.FeedbackScreen
import com.zoho.apptics.sample.ui.features.logging.LoggingScreen
import com.zoho.apptics.sample.ui.features.ratings.RatingsScreen
import com.zoho.apptics.sample.ui.features.remoteconfig.RemoteConfigScreen
import com.zoho.apptics.sample.ui.features.updates.UpdatesScreen
import com.zoho.apptics.sample.ui.features.userid.UserIdScreen
import com.zoho.apptics.sample.ui.home.HomeScreen

@Composable
fun AppticsSampleNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.Home.path) {
        composable(Route.Home.path) {
            HomeScreen(onNavigate = { route -> navController.navigate(route.path) })
        }
        composable(Route.UserId.path) {
            UserIdScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Analytics.path) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Crash.path) {
            CrashScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Logging.path) {
            LoggingScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Feedback.path) {
            FeedbackScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.RemoteConfig.path) {
            RemoteConfigScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Ratings.path) {
            RatingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Updates.path) {
            UpdatesScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.CrossPromo.path) {
            CrossPromoScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.ApiTracking.path) {
            ApiTrackingScreen(onBack = { navController.popBackStack() })
        }
    }
}
