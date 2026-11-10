package io.github.cleveroze.otherapps.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF6F4E37),
    onPrimary = Color(0xFFFFF8EF),
    primaryContainer = Color(0xFFEAD3B8),
    onPrimaryContainer = Color(0xFF2E1B10),
    secondary = Color(0xFF9A7455),
    onSecondary = Color(0xFFFFF8EF),
    secondaryContainer = Color(0xFFF0DCC6),
    onSecondaryContainer = Color(0xFF332014),
    tertiary = Color(0xFF6E7A55),
    onTertiary = Color(0xFFF9F8EE),
    tertiaryContainer = Color(0xFFDCE4C6),
    onTertiaryContainer = Color(0xFF1E2412),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7EFE2),
    onBackground = Color(0xFF33241A),
    surface = Color(0xFFFBF5EA),
    onSurface = Color(0xFF33241A),
    surfaceVariant = Color(0xFFEADBC7),
    onSurfaceVariant = Color(0xFF6F5A48),
    outline = Color(0xFFA08B76),
    outlineVariant = Color(0xFFDCCAB4),
    surfaceContainerLowest = Color(0xFFFFFDF8),
    surfaceContainerLow = Color(0xFFFAF2E6),
    surfaceContainer = Color(0xFFF4EADC),
    surfaceContainerHigh = Color(0xFFEEE1D0),
    surfaceContainerHighest = Color(0xFFE8D9C5),
    inverseSurface = Color(0xFF3B2B20),
    inverseOnSurface = Color(0xFFF7EFE2),
    inversePrimary = Color(0xFFE7C9A6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE7C9A6),
    onPrimary = Color(0xFF3F2A18),
    primaryContainer = Color(0xFF5A3E29),
    onPrimaryContainer = Color(0xFFFFE9D4),
    secondary = Color(0xFFD6B694),
    onSecondary = Color(0xFF3B2A1A),
    secondaryContainer = Color(0xFF4A3625),
    onSecondaryContainer = Color(0xFFF5DFC8),
    tertiary = Color(0xFFC3CFA6),
    onTertiary = Color(0xFF2C351D),
    tertiaryContainer = Color(0xFF3E4A2C),
    onTertiaryContainer = Color(0xFFDFEAC9),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF17110C),
    onBackground = Color(0xFFF2E4D3),
    surface = Color(0xFF1C150F),
    onSurface = Color(0xFFF2E4D3),
    surfaceVariant = Color(0xFF4A3A2C),
    onSurfaceVariant = Color(0xFFD2BDA6),
    outline = Color(0xFF9B836C),
    outlineVariant = Color(0xFF4F3E30),
    surfaceContainerLowest = Color(0xFF120C08),
    surfaceContainerLow = Color(0xFF1F1811),
    surfaceContainer = Color(0xFF241C14),
    surfaceContainerHigh = Color(0xFF2C231A),
    surfaceContainerHighest = Color(0xFF352A20),
    inverseSurface = Color(0xFFF2E4D3),
    inverseOnSurface = Color(0xFF3B2B20),
    inversePrimary = Color(0xFF6F4E37),
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1.2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.7).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun OtherAppsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
    SystemBars(darkTheme = darkTheme)
}

@Composable
private fun SystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
