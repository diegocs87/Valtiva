package com.example.valtiva.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.valtiva.presentation.converter.FavoritesSection
import com.example.valtiva.presentation.favorites.FavoritesUiEvent.FavoriteRemoved
import com.example.valtiva.presentation.theme.Dimens
import com.example.valtiva.presentation.theme.ValtivaTheme

/**
 * Renders [FavoritesUiState] and turns user interaction into [FavoritesUiEvent]s.
 *
 * Stateless by design, like [com.example.valtiva.presentation.converter.CurrencyConverterScreen]:
 * every value it shows comes from [state], every action it can trigger goes out through [onEvent].
 *
 * The cards themselves are [com.example.valtiva.presentation.converter.FavoritesSection] — the exact
 * composable the converter screen already uses for its own favorites list — rather than a rebuilt
 * copy, so both screens render a saved pair identically by construction and any future change to
 * the card only has to be made once.
 *
 * Picking a favorite here selects no pair on any form — this screen only lists and manages the
 * favorites, it does not convert — so that interaction is a no-op; removing one still writes
 * through [FavoritesViewModel] like every other change to the favorites list.
 */
@Composable
fun FavoritesScreen(
  state: FavoritesUiState,
  onEvent: (FavoritesUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  FavoritesSection(
    favorites = state.favorites,
    unavailable = state.unavailable,
    onFavoriteSelected = {},
    onFavoriteRemoved = { onEvent(FavoriteRemoved(it)) },
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(Dimens.ScreenPadding),
  )
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
  ValtivaTheme {
    FavoritesScreen(state = FavoritesUiState(), onEvent = {})
  }
}
