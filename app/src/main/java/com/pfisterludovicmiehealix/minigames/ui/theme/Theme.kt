package com.pfisterludovicmiehealix.minigames.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary        = AppBlue,
    onPrimary      = AppWhite,
    secondary      = AppGreen,
    onSecondary    = AppBlack,
    background     = AppBlack,
    onBackground   = AppWhite,
    surface        = AppSurface,
    onSurface      = AppWhite,
    outline        = AppBorder,
)

@Composable
fun MiniGamesAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}