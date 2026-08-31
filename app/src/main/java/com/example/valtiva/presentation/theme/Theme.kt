package com.example.valtiva.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Colors [MaterialTheme.colorScheme] has no slot for, such as a semantic "success" accent.
 *
 * Kept as its own small token set instead of overloading, say, `tertiary` for success: a future
 * reader should not have to remember what a generic Material slot was repurposed to mean here.
 */
data class ValtivaExtendedColors(
  val success: Color,
  val onSuccess: Color,
  val successContainer: Color,
)

private val LightExtendedColors = ValtivaExtendedColors(
  success = Success,
  onSuccess = Color.White,
  successContainer = SuccessContainer,
)

private val DarkExtendedColors = ValtivaExtendedColors(
  success = SuccessDark,
  onSuccess = Color(0xFF052E14),
  successContainer = SuccessContainerDark,
)

private val LocalValtivaExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/** Reads [ValtivaExtendedColors], the same way [MaterialTheme.colorScheme] is read. */
object ValtivaColors {
  val current: ValtivaExtendedColors
    @Composable get() = LocalValtivaExtendedColors.current
}

private val LightColorScheme = lightColorScheme(
  primary = Primary,
  onPrimary = Color.White,
  primaryContainer = PrimaryContainerLight,
  onPrimaryContainer = PrimaryDark,
  secondary = TextSecondary,
  onSecondary = Color.White,
  background = Background,
  onBackground = TextPrimary,
  surface = Surface,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceVariant,
  onSurfaceVariant = TextSecondary,
  error = ErrorColor,
  onError = Color.White,
  errorContainer = ErrorContainer,
  onErrorContainer = ErrorColor,
  outline = Divider,
  outlineVariant = Divider,
)

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryDarkTheme,
  onPrimary = Color(0xFF06192F),
  primaryContainer = PrimaryContainerDark,
  onPrimaryContainer = PrimaryDarkTheme,
  secondary = TextSecondaryDark,
  onSecondary = Color(0xFF0B1220),
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextSecondaryDark,
  error = ErrorDark,
  onError = Color(0xFF450A0A),
  errorContainer = ErrorContainerDark,
  onErrorContainer = ErrorDark,
  outline = DividerDark,
  outlineVariant = DividerDark,
)

private val ValtivaShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun ValtivaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Off by default: a per-device Material You palette would override the fintech brand colors
  // this theme exists to make consistent. Left as a parameter so it can still be opted into.
  dynamicColor: Boolean = false,
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
  val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

  CompositionLocalProvider(LocalValtivaExtendedColors provides extendedColors) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, shapes = ValtivaShapes, content = content)
  }
}
