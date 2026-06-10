package com.tunalex.sportmap.ui.theme

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
    primary = BlueVibrant,
    onPrimary = White,
    primaryContainer = SkyLight,
    onPrimaryContainer = IndigoDeep,
    secondary = BlueMedium,
    onSecondary = White,
    secondaryContainer = BlueLight,
    onSecondaryContainer = GrayDark,
    tertiary = IndigoDeep,
    onTertiary = White,
    background = SurfaceLight,
    onBackground = GrayDark,
    surface = White,
    onSurface = GrayDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = GrayDark,
    error = RedDanger,
    onError = White,
    outline = GrayMedium,
)

private val DarkColors = darkColorScheme(
    primary = BlueLight,
    onPrimary = GrayDark,
    primaryContainer = IndigoDeep,
    onPrimaryContainer = SkyLight,
    secondary = BlueMedium,
    onSecondary = White,
    secondaryContainer = GrayDark,
    onSecondaryContainer = SkyLight,
    tertiary = SkyLight,
    onTertiary = GrayDark,
    background = Black,
    onBackground = OffWhite,
    surface = SurfaceDark,
    onSurface = OffWhite,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = GrayLight,
    error = RedDanger,
    onError = White,
    outline = GrayMedium,
)

@Composable
fun SportMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Material You) is OFF by default so we always show the brand palette.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SportMapTypography,
        content = content
    )
}
