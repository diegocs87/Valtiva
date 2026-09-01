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
}
