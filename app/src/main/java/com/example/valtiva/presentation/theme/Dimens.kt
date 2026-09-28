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

  /**
   * Sizes the currency picker's lazy list with an exact width AND height (`Modifier.size(...)`),
   * never `heightIn`/`fillMaxWidth`. `DropdownMenu` sizes its content with
   * `Modifier.width(IntrinsicSize.Max)`, which queries every child's intrinsic width *and* height
   * unless that child's own modifier reports a fixed value without asking it — a `LazyColumn`
   * (built on `SubcomposeLayout`) cannot answer an intrinsics query on either axis, so any
   * non-exact modifier (`heightIn(max=)`, `fillMaxWidth()`) forwards the query into it and crashes.
   * 280.dp matches Material's own default max width for a [androidx.compose.material3.DropdownMenu].
   */
  val CurrencyMenuWidth = 280.dp
  val CurrencyMenuItemHeight = 56.dp

  /** Never show more rows than this even when a chip near the top of the screen has room to spare. */
  const val CurrencyMenuMaxRowCount = 8

  /**
   * Space to leave above the list when sizing it to fit under a chip: the search field itself, the
   * menu content's own vertical padding, and the margin `DropdownMenu` always keeps clear from the
   * screen's top/bottom edges when deciding whether a placement fits. Material3 doesn't expose
   * those constants, so 240dp was calibrated empirically (on-device, by shrinking the list until it
   * stopped flipping away from the anchor, with a further margin added since that boundary leaves
   * only a few dp of slack — not enough to survive overhead this estimate doesn't account for, like
   * system bar insets) rather than derived — an underestimate here is what makes the menu jump to
   * the top of the screen instead of sitting a little shorter than the on-screen budget allows.
   */
  val CurrencyMenuReservedHeaderHeight = 240.dp
}
