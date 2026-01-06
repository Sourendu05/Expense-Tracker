package com.example.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen80,
    secondary = DarkGreen60,
    tertiary = DarkGreen40,
    background = DarkGreen5,
    surface = DarkGreen10,
    surfaceVariant = DarkGreen20,
    onPrimary = NeutralLight,
    onSecondary = NeutralLight,
    onTertiary = NeutralDark,
    onBackground = NeutralLight,
    onSurface = NeutralLight,
    onSurfaceVariant = Green40,
    error = DarkRed80,
    onError = NeutralLight,
    errorContainer = DarkRed20,
    onErrorContainer = Red40
)

private val LightColorScheme = lightColorScheme(
    primary = Green80,
    secondary = Green60,
    tertiary = Green40,
    background = Green10,
    surface = NeutralLight,
    surfaceVariant = Green20,
    onPrimary = NeutralLight,
    onSecondary = NeutralDark,
    onTertiary = NeutralDark,
    onBackground = NeutralDark,
    onSurface = NeutralDark,
    onSurfaceVariant = NeutralGray,
    error = Red80,
    onError = NeutralLight,
    errorContainer = Red10,
    onErrorContainer = Red80
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled by default to use our custom theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}