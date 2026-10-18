package com.blyndchat.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush

object ThemeState {
    var currentMode by mutableStateOf("black")
}

data class AppThemeColors(
    val isDark: Boolean,
    val topBarContainer: Color,
    val topBarTitle: Color,
    val topBarSubtitle: Color,
    val screenBackgroundBrush: Brush,
    val bottomBarContainer: Color,
    val bottomBarSelectedIndicator: Color,
    val bottomBarSelectedIcon: Color,
    val bottomBarUnselected: Color,
    val cardContainer: Color,
    val cardBorder: Brush,
    val cardBorderSubtle: Color,
    val cardShadowSpot: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val badgeBackground: Color,
    val badgeBorder: Color,
    val scrimColors: List<Color>,
    val traitChipContainer: Color,
    val traitChipText: Color,
    val traitChipBorder: Color,
    val skipButtonContainer: Color,
    val skipButtonBorder: Color,
    val skipButtonIcon: Color,
    val chatBubblePeerContainer: Color,
    val chatBubblePeerBorder: Color,
    val chatBubblePeerText: Color,
    val chatInputContainer: Color,
    val chatInputFieldContainer: Color,
    val chatInputText: Color,
    val chatInputPlaceholder: Color,
    val chatInputBorder: Color,
    val detailTileContainer: Color,
    val detailTileBorder: Color,
    val detailTileLabel: Color,
    val detailTileValue: Color,
    val emptyStateCard: Color,
    val emptyStateBorder: Color,
    val dividerColor: Color
)

val DarkAppColors = AppThemeColors(
    isDark = true,
    topBarContainer = Color(0xFF0F081C),
    topBarTitle = Color.White,
    topBarSubtitle = LuxuryGold,
    screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0618),
            Color(0xFF140924),
            Color(0xFF0A0413)
        )
    ),
    bottomBarContainer = Color(0xFF0F071D),
    bottomBarSelectedIndicator = LuxuryGold,
    bottomBarSelectedIcon = Color(0xFF140826),
    bottomBarUnselected = Color(0xFF8E82A6),
    cardContainer = Color(0xFF160B29),
    cardBorder = Brush.linearGradient(
        colors = listOf(
            LuxuryGold.copy(alpha = 0.8f),
            LuxuryRose.copy(alpha = 0.7f),
            Color(0xFF9D4EDD).copy(alpha = 0.6f),
            LuxuryGold.copy(alpha = 0.8f)
        )
    ),
    cardBorderSubtle = Color(0x33FFFFFF),
    cardShadowSpot = LuxuryRose.copy(alpha = 0.35f),
    textPrimary = Color.White,
    textSecondary = Color(0xFFC7B8E0),
    textMuted = Color(0xFF8E82A6),
    badgeBackground = Color(0xCC0D0618),
    badgeBorder = LuxuryGold.copy(alpha = 0.6f),
    scrimColors = listOf(
        Color.Transparent,
        Color(0x99160B29),
        Color(0xFF160B29)
    ),
    traitChipContainer = Color(0xFF22133D),
    traitChipText = Color(0xFFF0EBF8),
    traitChipBorder = Color(0x33FFD700),
    skipButtonContainer = Color(0xFF24143D),
    skipButtonBorder = Color(0x44FFFFFF),
    skipButtonIcon = Color(0xFFFF6584),
    chatBubblePeerContainer = Color(0xFF1D1135),
    chatBubblePeerBorder = Color(0x33FFD700),
    chatBubblePeerText = Color(0xFFF0EBF8),
    chatInputContainer = Color(0xFF0F071D),
    chatInputFieldContainer = Color(0xFF190E2E),
    chatInputText = Color.White,
    chatInputPlaceholder = Color(0xFF8E82A6),
    chatInputBorder = Color(0x44FFFFFF),
    detailTileContainer = Color(0xFF160B29),
    detailTileBorder = Color(0x26FFD700),
    detailTileLabel = Color(0xFF8E82A6),
    detailTileValue = Color.White,
    emptyStateCard = Color(0xFF160B29),
    emptyStateBorder = Color(0x33FFD700),
    dividerColor = Color(0x22FFFFFF)
)

val WhiteAppColors = AppThemeColors(
    isDark = false,
    topBarContainer = Color(0xFFFFFFFF),
    topBarTitle = Color(0xFF150D24),
    topBarSubtitle = Color(0xFFB33664),
    screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF9FAFD),
            Color(0xFFF2F4F9),
            Color(0xFFECEFF6)
        )
    ),
    bottomBarContainer = Color(0xFFFFFFFF),
    bottomBarSelectedIndicator = Color(0xFFFFE58F),
    bottomBarSelectedIcon = Color(0xFF332002),
    bottomBarUnselected = Color(0xFF847E91),
    cardContainer = Color(0xFFFFFFFF),
    cardBorder = Brush.linearGradient(
        colors = listOf(
            ShaadiGold.copy(alpha = 0.7f),
            LuxuryRose.copy(alpha = 0.5f),
            ShaadiPinkLight.copy(alpha = 0.4f),
            ShaadiGold.copy(alpha = 0.7f)
        )
    ),
    cardBorderSubtle = Color(0x18000000),
    cardShadowSpot = Color(0x14000000),
    textPrimary = Color(0xFF171226),
    textSecondary = Color(0xFF5B536E),
    textMuted = Color(0xFF8A829D),
    badgeBackground = Color(0xF2FFFFFF),
    badgeBorder = ShaadiGold.copy(alpha = 0.8f),
    scrimColors = listOf(
        Color.Transparent,
        Color(0x80FFFFFF),
        Color(0xFFFFFFFF)
    ),
    traitChipContainer = Color(0xFFF4EEFB),
    traitChipText = Color(0xFF3C2758),
    traitChipBorder = Color(0x33C9A961),
    skipButtonContainer = Color(0xFFF4F3F8),
    skipButtonBorder = Color(0x22000000),
    skipButtonIcon = Color(0xFFFF3366),
    chatBubblePeerContainer = Color(0xFFF1EDF9),
    chatBubblePeerBorder = Color(0x33D64978),
    chatBubblePeerText = Color(0xFF1C132E),
    chatInputContainer = Color(0xFFFFFFFF),
    chatInputFieldContainer = Color(0xFFF5F4F9),
    chatInputText = Color(0xFF171226),
    chatInputPlaceholder = Color(0xFF8A829D),
    chatInputBorder = Color(0x22000000),
    detailTileContainer = Color(0xFFFFFFFF),
    detailTileBorder = Color(0x18000000),
    detailTileLabel = Color(0xFF6E6782),
    detailTileValue = Color(0xFF171226),
    emptyStateCard = Color(0xFFFFFFFF),
    emptyStateBorder = Color(0x18000000),
    dividerColor = Color(0xFFE8E7EE)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val DarkColorScheme = darkColorScheme(
    primary = ShaadiPink,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF121212)
)

private val LightColorScheme = lightColorScheme(
    primary = ShaadiPink,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Explicit pure black scheme for user-selected "black" theme
private val BlackColorScheme = darkColorScheme(
    primary = ShaadiPink,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = androidx.compose.ui.graphics.Color.Black,
    surface = androidx.compose.ui.graphics.Color.Black,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun BlyndChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    // Optional explicit theme mode override: "white" or "black"
    themeMode: String? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeMode == "black" -> BlackColorScheme
        themeMode == "white" -> LightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            val isDark = themeMode == "black" || darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val FancyDarkScheme: ColorScheme = darkColorScheme(
    primary = LuxuryRose,
    onPrimary = Color.White,
    secondary = LuxuryGold,
    onSecondary = LuxuryObsidian,
    tertiary = ShaadiPurple,
    onTertiary = Color.White,
    background = LuxuryObsidian,
    onBackground = LuxuryTextPrimary,
    surface = LuxurySurface,
    onSurface = LuxuryTextPrimary,
    surfaceVariant = LuxurySurfaceVariant,
    onSurfaceVariant = LuxuryTextSecondary,
    error = ShaadiRed,
    onError = Color.White
)

private val FancyLightScheme: ColorScheme = lightColorScheme(
    primary = LuxuryRose,
    onPrimary = Color.White,
    secondary = ShaadiGold,
    onSecondary = Color(0xFF140D22),
    tertiary = ShaadiPurple,
    onTertiary = Color.White,
    background = Color(0xFFF9FAFD),
    onBackground = Color(0xFF171226),
    surface = Color.White,
    onSurface = Color(0xFF171226),
    surfaceVariant = Color(0xFFF0EDF7),
    onSurfaceVariant = Color(0xFF5B536E),
    error = ShaadiRed,
    onError = Color.White
)

@Composable
fun BlyndAppTheme(
    themeMode: String = ThemeState.currentMode,
    content: @Composable () -> Unit
) {
    val isDark = themeMode != "white"
    val appColors = if (isDark) DarkAppColors else WhiteAppColors
    val colorScheme = if (isDark) FancyDarkScheme else FancyLightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusBarColor = if (isDark) LuxuryObsidian else Color.White
            val navBarColor = if (isDark) LuxuryObsidian else Color.White
            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = navBarColor.toArgb()
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !isDark
            insets.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun FancyTheme(content: @Composable () -> Unit) {
    BlyndAppTheme(themeMode = "black", content = content)
}
