package com.example.valtiva.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.valtiva.R

/**
 * The top-level sections of the app, in the order the bottom bar shows them.
 *
 * An enum rather than navigation routes: these are pages of one screen the user swipes between, not
 * destinations that go on a back stack, so there is nothing to serialize and nothing to navigate to.
 * The nested [com.example.valtiva.presentation.navigation.MainRoute] graph still owns the app's real
 * navigation; this only orders the tabs.
 *
 * [CONVERTER] and [FAVORITES] are the only sections with a screen behind them today. The rest are
 * declared here because the bottom bar is part of this iteration's design, and a bar with two items
 * is not one — they render an explicit "not built yet" page rather than a plausible-looking empty
 * state that would read as a feature that is merely out of data.
 */
enum class ValtivaDestination(
  @param:StringRes val labelRes: Int,
  val icon: ImageVector,
) {
  CONVERTER(R.string.destination_converter, Icons.Filled.SwapHoriz),
  FAVORITES(R.string.destination_favorites, Icons.Filled.Star),
  HISTORY(R.string.destination_history, Icons.Filled.History),
  SETTINGS(R.string.destination_settings, Icons.Filled.Settings),
}
