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

  val SwapButtonSize = 48.dp
  val MinTouchTarget = 48.dp
  val IconSizeMedium = 24.dp
  val IconSizeSmall = 18.dp
  val CurrencyBadgeSize = 36.dp

  /** The currency selector is a primary control, so its flag and its height are sized as one. */
  val CurrencyFlagChipSize = 30.dp
  val CurrencySelectorHeight = 52.dp

  val ActionButtonHeight = 48.dp
  val ActionButtonElevation = 3.dp

  val SettingsIconContainerSize = 40.dp
  val SettingsLogoSize = 96.dp

  /** Rows the top-anchored currency panel aims to show at once (capped by the screen's height). */
  const val CurrencySheetMaxVisibleRows = 15
  val CurrencySheetItemHeight = 44.dp
  val CurrencySheetFlagSize = 28.dp

  /**
   * Height the panel needs besides its list: status bar, search field, bottom padding, plus a
   * margin so it never touches the bottom edge of the screen.
   */
  val CurrencySheetReservedHeight = 160.dp
}
