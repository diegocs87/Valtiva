package com.example.valtiva.presentation.theme

import androidx.compose.ui.unit.dp

/** Centralized spacing and sizing so no composable invents its own magic numbers. */
object Dimens {
  val SpaceXs = 4.dp
  val SpaceSm = 8.dp
  val SpaceMd = 12.dp
  val SpaceLg = 16.dp
  val SpaceXl = 24.dp
  val SpaceXxl = 32.dp

  val ScreenPadding = 20.dp
  val CardPadding = 20.dp

  /**
   * Widest a screen's content column gets. Past this a single column of text and cards stretches
   * into lines too long to scan, so wider windows center the column instead of filling them.
   */
  val ContentMaxWidth = 640.dp

  /** Same idea for dialogs and the top currency panel, which read as sheets, not as screens. */
  val DialogMaxWidth = 560.dp

  /** Window width from which the home shell swaps its bottom bar for a side rail. */
  val NavigationRailBreakpoint = 600.dp

  /** Window width from which the converter lays its form and its favorites out side by side. */
  val TwoPaneBreakpoint = 840.dp
  val TwoPaneMaxWidth = 1120.dp

  /**
   * Width, in the user's own text size (dp divided by the font scale), below which a row of
   * controls no longer has room for everything: the amount and its currency chip stack, and a
   * favorite drops its decorations. Measured against text size rather than raw dp because a
   * 360dp phone at 2x font has the room of a 180dp one.
   */
  val CompactCardWidth = 250.dp
  val CompactRowWidth = 300.dp

  /** Longest the currency-name caption under a chip may grow before it wraps, so it cannot squeeze the amount. */
  val CurrencyNameMaxWidth = 140.dp

  val SwapButtonSize = 48.dp
  val MinTouchTarget = 48.dp
  val IconSizeMedium = 24.dp
  val IconSizeSmall = 18.dp
  val CurrencyBadgeSize = 36.dp

  /** The currency selector is a primary control, so its flag and its height are sized as one. */
  val CurrencyFlagChipSize = 30.dp
  val CurrencySelectorHeight = 52.dp

  val PrimaryButtonHeight = 56.dp
  val ActionButtonHeight = 48.dp
  val ActionButtonElevation = 3.dp

  val SettingsIconContainerSize = 40.dp
  val SettingsLogoSize = 96.dp
  val EmptyStateIconSize = 48.dp

  /** Rows the top-anchored currency panel aims to show at once (capped by the room above the keyboard). */
  const val CurrencySheetMaxVisibleRows = 15

  /** A row's minimum height: it grows past this when the user's font scale needs more room. */
  val CurrencySheetItemHeight = 48.dp
  val CurrencySheetFlagSize = 28.dp

  /** Breathing room kept between the top panel and the bottom edge of the space it may use. */
  val CurrencySheetBottomMargin = 16.dp
}
