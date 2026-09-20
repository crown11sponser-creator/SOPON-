package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldAmber,
    onSecondary = Color.Black,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = BlueAccent,
    background = Navy900,
    onBackground = Color.White,
    surface = Navy800,
    onSurface = Color.White,
    surfaceVariant = Navy700,
    onSurfaceVariant = Slate400,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDark,
    secondary = GoldAmber,
    onSecondary = Color.Black,
    secondaryContainer = GoldLight,
    onSecondaryContainer = GoldDark,
    tertiary = BlueAccent,
    background = Slate50,
    onBackground = Navy900,
    surface = Color.White,
    onSurface = Navy900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our fintech custom palette by default for brand consistency
    content: @Composable () -> Unit,
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
