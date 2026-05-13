package com.antharjala.watch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AntharJalaColorScheme = darkColorScheme(
    primary                = Primary,
    onPrimary              = OnPrimary,
    primaryContainer       = PrimaryContainer,
    onPrimaryContainer     = OnPrimaryContainer,
    secondary              = Secondary,
    onSecondary            = OnSecondary,
    secondaryContainer     = SecondaryContainer,
    onSecondaryContainer   = OnSecondaryContainer,
    background             = Background,
    onBackground           = OnBackground,
    surface                = Surface,
    onSurface              = OnSurface,
    surfaceVariant         = SurfaceVariant,
    onSurfaceVariant       = OnSurfaceVariant,
    error                  = Error,
    onError                = OnError,
    errorContainer         = ErrorContainer,
    onErrorContainer       = OnErrorContainer,
    outline                = Outline,
    outlineVariant         = OutlineVariant
)

@Composable
fun AntharJalaWatchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AntharJalaColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
