package com.zoho.apptics.sample.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Brand40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = BrandContainerLight,
    onPrimaryContainer = Brand20,
    secondary = Accent40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = SurfaceLight,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1B1C1F),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1B1C1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFEEF0F6),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF44464E),
    outline = OutlineSoftLight,
    error = Danger
)

private val DarkColors = darkColorScheme(
    primary = Brand80,
    onPrimary = Brand20,
    primaryContainer = BrandContainerDark,
    onPrimaryContainer = BrandContainerLight,
    secondary = Accent80,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF1B1C1F),
    background = SurfaceDark,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE4E6EC),
    surface = androidx.compose.ui.graphics.Color(0xFF181B22),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE4E6EC),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF22262E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC5C7CD),
    outline = OutlineSoftDark,
    error = Danger
)

@Composable
fun AppticsSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppticsTypography,
        content = content
    )
}
